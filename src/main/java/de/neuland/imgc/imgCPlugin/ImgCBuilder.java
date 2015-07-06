package de.neuland.imgc.imgCPlugin;

import de.neuland.imgc.command.ICommandExecutor;
import de.neuland.imgc.command.InvalidXMLException;
import de.neuland.imgc.command.impl.CommandExecutorFactory;
import de.neuland.imgc.imgCPlugin.Messages;
import de.neuland.imgc.properties.SystemPropertiesReader;
import de.neuland.imgc.validator.IImgCValidator;
import de.neuland.imgc.validator.impl.ImgCValidator;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * WARNING ! required java runtime version 1.7 or higher
 */
public final class ImgCBuilder extends Builder {

    private static final String IMG_C_WORKAROUND= "-useInternalIgnore=true";

    private final String imgCPath;
    private final String executeDirectory; // = "/var/lib/jenkins/hybris/bin/extensions/";
    private final String configurationFile;
    private final String cacheFile;
    private final String ignorePath;
    private final String startingWarning; // Wert ab dem der Build gelb wird
    private final String startingFail; // Wert ab dem der Build rot wird
    private final boolean useInternalIgnoreAndConfig;
    private IImgCValidator validator;
    private CommandExecutorFactory commandExecutorFactory;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public ImgCBuilder(String imgCPath, String configurationFile, String cacheFile, String ignorePath,
                       String executeDirectory, String startingWarning, String startingFail, boolean useInternalIgnoreAndConfig) {
        this.imgCPath = imgCPath;
        this.configurationFile = configurationFile;
        this.cacheFile = cacheFile;
        this.ignorePath = ignorePath;
        this.executeDirectory = executeDirectory;
        this.startingWarning = startingWarning;
        this.startingFail = startingFail;
        this.useInternalIgnoreAndConfig = useInternalIgnoreAndConfig;
        this.validator = new ImgCValidator();
        this.commandExecutorFactory = new CommandExecutorFactory();
    }

    /** Getter for <tt>config.jelly</tt>. - imgCPath */
    @SuppressWarnings("unused")
    public final String getImgCPath() { return imgCPath; }

    /** Getter for <tt>config.jelly</tt>. - configurationFile */
    @SuppressWarnings("unused")
    public final String getConfigurationFile() { return configurationFile; }

    /** Getter for <tt>config.jelly</tt>. - cacheFile */
    @SuppressWarnings("unused")
    public final String getCacheFile() { return cacheFile; }

    /** Getter for <tt>config.jelly</tt>. - ignorePath */
    @SuppressWarnings("unused")
    public final String getIgnorePath() { return ignorePath; }

    /** Getter for <tt>config.jelly</tt>. - executeDirectory */
    @SuppressWarnings("unused")
    public final String getExecuteDirectory() { return executeDirectory; }

    /** Getter for <tt>config.jelly</tt>. - startingWarning */
    @SuppressWarnings("unused")
    public final String getStartingWarning() { return startingWarning; }

    /** Getter for <tt>config.jelly</tt>. - startingFail*/
    @SuppressWarnings("unused")
    public final String getStartingFail() { return startingFail; }


    /** Getter for <tt>config.jelly</tt>. - useInternalIgnoreAndConfig*/
    @SuppressWarnings("unused")
    public final boolean getUseInternalIgnoreAndConfig() { return useInternalIgnoreAndConfig; }
    
    @Override
    public final boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) {

        boolean inputsAreValid = validateInputs(listener);
        if(!inputsAreValid){
            build.setResult(Result.FAILURE);
            return false;
        }

        ICommandExecutor commandExecutor = commandExecutorFactory.createCommandExecutor(listener, build.getArtifactsDir().getName());

        boolean success = commandExecutor.copyImageCrawler(executeDirectory,imgCPath);
        if(!success){
            listener.getLogger().println(Messages.ImgCBuilder_perform_ERROR_copyImageCrawler());
            build.setResult(Result.FAILURE);
            return false;
        }

        final List<String> imgCArgs = createImgCArgs();
        success = commandExecutor.runImgCrawler(executeDirectory,imgCPath,imgCArgs);
        if(!success){
            listener.getLogger().println(Messages.ImgCBuilder_perform_ERROR_runImgCrawler());
            build.setResult(Result.FAILURE);
            return false;
        }
        String reportFilesName = SystemPropertiesReader.readImgCReportFilesName();
        String imgCResultXml = reportFilesName+".xml";
        success = commandExecutor.saveImageCrawlerResultAsArtifact(executeDirectory, build.getRootDir().getAbsolutePath(), executeDirectory+File.separator+imgCResultXml);
        if(!success){
            listener.getLogger().println(Messages.ImgCBuilder_perform_ERROR_saveImageCrawlerResultAsArtifact_xml());
            build.setResult(Result.FAILURE);
            return false;
        }

        String imgCResultHtml = reportFilesName+".html";
        success = commandExecutor.saveImageCrawlerResultAsArtifact(executeDirectory, build.getRootDir().getAbsolutePath(), executeDirectory+File.separator+imgCResultHtml);
        if(!success){
            listener.getLogger().println(Messages.ImgCBuilder_perform_ERROR_saveImageCrawlerResultAsArtifact_html());
            build.setResult(Result.FAILURE);
            return false;
        }

        final int result;
        try {
            result = commandExecutor.analyseImageCrawlerResult(executeDirectory+ File.separator+imgCResultXml);
        } catch (InvalidXMLException e) {
            listener.getLogger().println(Messages.ImgCBuilder_perform_ERROR_analyseImageCrawlerResult());
            e.printStackTrace(listener.getLogger());
            build.setResult(Result.FAILURE);
            return false;
        }

    	if(result < 0){
    		build.setResult(Result.FAILURE);
    		return false;
    	}

    	final int startingFailInt = stringToInt(startingFail);
    	if(startingFailInt != -1 && result >= startingFailInt){
    		String msg = Messages.ImgCBuilder_perform_INFO_found_start_FAILURE()+result+Messages._ImgCBuilder_perform_INFO_found_mid_FAILURE()+startingFail+Messages._ImgCBuilder_perform_INFO_found_end_FAILURE();
    		listener.getLogger().println(msg);
        	build.setResult(Result.FAILURE);
        	return false;
    	}

    	final int startingWarningInt = stringToInt(startingWarning);
    	if(startingWarningInt != -1 && result >= startingWarningInt){
    		String msg = Messages.ImgCBuilder_perform_INFO_found_start_UNSTABLE()+result+Messages._ImgCBuilder_perform_INFO_found_mid_UNSTABLE()+startingWarningInt+Messages._ImgCBuilder_perform_INFO_found_end_UNSTABLE();
    		listener.getLogger().println(msg);
        	build.setResult(Result.UNSTABLE); // return false => BuildFAILED
    	}
        return true;
    }

    @Override
    public final DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    private List<String> createImgCArgs() {
        List<String> args = new ArrayList<>();
        args.add(SystemPropertiesReader.readImgCReportFileNameArgsKey()+SystemPropertiesReader.readImgCReportFilesName());
        if(configurationFile != null && !configurationFile.equals("")){
            args.add(SystemPropertiesReader.readImgCConfigurationFileArgsKey()+configurationFile);
        }
        String cacheFileArg = SystemPropertiesReader.readImgCCacheFileArgsKey();
        if(cacheFile == null || cacheFile.equals("")){
            cacheFileArg += SystemPropertiesReader.readImgCCacheFileDefault();
        }else{
            cacheFileArg += cacheFile;
        }
        args.add(cacheFileArg);
        String ignorePathArg = SystemPropertiesReader.readImgCIgnorePathArgsKey();
        if(ignorePath == null || ignorePath.equals("")){
            ignorePathArg += SystemPropertiesReader.readImgCIgnorePathDefault();
        }else{
            ignorePathArg += ignorePath;
        }
        args.add(ignorePathArg);
        if(useInternalIgnoreAndConfig){
            args.add(IMG_C_WORKAROUND);
        }
        return args;
    }

	private int stringToInt(String s){
		return (s == null || !s.matches("-?\\d++")) ? 0 : Integer.valueOf(s);
	}

    private boolean validateInputs(final BuildListener listener){
        if(!validator.isValidImgCPath(imgCPath)){
            listener.getLogger().println(Messages.ImgCBuilder_ERROR_invalid_imgC_path());
            return false;
        }
        if (!useInternalIgnoreAndConfig && !validator.isValidConfigurationFile(configurationFile)){
            listener.getLogger().println(Messages.ImgCBuilder_ERROR_invalid_configuration_file());
            return false;
        }

        if (!validator.isValidCacheFile(cacheFile)){
            listener.getLogger().println(Messages.ImgCBuilder_ERROR_invalid_cache_file());
            return false;
        }
        if (!useInternalIgnoreAndConfig && !validator.isValidIgnoreDir(ignorePath)){
            listener.getLogger().println(Messages.ImgCBuilder_ERROR_invalid_ignore_path());
            return false;
        }
        if(!validator.isValidExecuteDirectory(executeDirectory)){
            listener.getLogger().println(Messages.ImgCBuilder_ERROR_invalid_execute_directory());
            return false;
        }
        if (!validator.isValidStartingWarning(startingWarning)) {
            listener.getLogger().println(Messages.ImgCBuilder_ERROR_invalid_startingWarning_not_a_positive_integer_or_default());
            return false;
        }
        if (!validator.isValidStartingFail(startingFail)){
            listener.getLogger().println(Messages.ImgCBuilder_ERROR_invalid_startingWarning_not_a_positive_integer_or_default());
            return false;
        }
        return true;
    }

    // package-private for tests
    void setCommandExecutorFactory(CommandExecutorFactory commandExecutorFactory){
        this.commandExecutorFactory = commandExecutorFactory;
    }

    // package-private for tests
    void setIImgCValidator(IImgCValidator validator){
        this.validator = validator;
    }

    /**
     * Descriptor for {@link ImgCBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Getter fuer den Namen der Buildanwendung
         */
        public final String getDisplayName() {
            return "ImgC";
        }

        /** Validation for <tt>config.jelly</tt>. - imgCPath */
        @SuppressWarnings("unused")
        public final FormValidation doCheckImgCPath(@QueryParameter final String value) throws IOException, ServletException {
        	if (!new ImgCValidator().isValidImgCPath(value)){
        		return FormValidation.error(Messages.ImgCBuilder_ERROR_invalid_imgC_path());
        	}
            return FormValidation.ok();
        }

        /** Validation for <tt>config.jelly</tt>. - configurationFile */
        @SuppressWarnings("unused")
        public final FormValidation doCheckConfigurationFile(@QueryParameter("configurationFile") final String configurationFile,
                                                             @QueryParameter("useInternalIgnoreAndConfig") final boolean useInternalIgnoreAndConfig) throws IOException, ServletException {
            if (!useInternalIgnoreAndConfig && !new ImgCValidator().isValidConfigurationFile(configurationFile)){
                return FormValidation.error(Messages.ImgCBuilder_ERROR_invalid_configuration_file());
            }
            return FormValidation.ok();
        }

        /** Validation for <tt>config.jelly</tt>. - cacheFile */
        @SuppressWarnings("unused")
        public final FormValidation doCheckCacheFile(@QueryParameter final String value) throws IOException, ServletException {
            if (!new ImgCValidator().isValidCacheFile(value)){
                return FormValidation.error(Messages.ImgCBuilder_ERROR_invalid_cache_file());
            }
            return FormValidation.ok();
        }

        /** Validation for <tt>config.jelly</tt>. - ignorePath */
        @SuppressWarnings("unused")
        public final FormValidation doCheckIgnorePath(@QueryParameter("ignorePath") final String ignorePath,
                                                      @QueryParameter("useInternalIgnoreAndConfig") final boolean useInternalIgnoreAndConfig) throws IOException, ServletException {
            if (!useInternalIgnoreAndConfig && !new ImgCValidator().isValidIgnoreDir(ignorePath)){
                return FormValidation.error(Messages.ImgCBuilder_ERROR_invalid_ignore_path());
            }
            return FormValidation.ok();
        }

        /** Validation for <tt>config.jelly</tt>. - executeDirectory */
        @SuppressWarnings("unused")
        public final FormValidation doCheckExecuteDirectory(@QueryParameter("executeDirectory") final String executeDirectory) throws IOException, ServletException {
            if (!new ImgCValidator().isValidExecuteDirectory(executeDirectory)){
                return FormValidation.error(Messages.ImgCBuilder_ERROR_invalid_execute_directory());
            }
            return FormValidation.ok();
        }

        /** Validation for <tt>config.jelly</tt>. - startingFail*/
        @SuppressWarnings("unused")
        public final FormValidation doCheckStartingFail(@QueryParameter("startingFail") final String startingFail) throws IOException, ServletException {
            if (!new ImgCValidator().isValidStartingFail(startingFail)){
                return FormValidation.error(Messages.ImgCBuilder_ERROR_invalid_startingFail_not_a_positive_integer_or_default());
            }
            return FormValidation.ok();
        }

        /** Validation for <tt>config.jelly</tt>. - startingWarning */
        @SuppressWarnings("unused")
        public final FormValidation doCheckStartingWarning(@QueryParameter("startingWarning") final String startingWarning) throws IOException, ServletException {
            if (!new ImgCValidator().isValidStartingWarning(startingWarning)) {
                return FormValidation.error(Messages.ImgCBuilder_ERROR_invalid_startingWarning_not_a_positive_integer_or_default());
            }
            return FormValidation.ok();
        }

        @Override
        public final boolean configure(final StaplerRequest req, final JSONObject formData) throws FormException {
            save();
            return super.configure(req,formData);
        }

        @Override
        public final boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> aClass) {
            return true; // Indicates that this builder can be used with all kinds of project types - comment out of the Jenkins-API
        }

    }
}