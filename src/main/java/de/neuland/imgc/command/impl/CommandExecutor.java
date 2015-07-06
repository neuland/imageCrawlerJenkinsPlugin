package de.neuland.imgc.command.impl;

import de.neuland.imgc.command.InvalidXMLException;
import de.neuland.imgc.command.impl.xml.ImageCrawlerReport;
import de.neuland.imgc.command.ICommandExecutor;
import de.neuland.imgc.streamwrapper.LoggingOutputStream;
import hudson.model.BuildListener;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

public class CommandExecutor implements ICommandExecutor{

    private final static String log4JConfigResourcePath = File.separator+"de"+File.separator+"neuland"+File.separator+"imgc"+File.separator+"command"+File.separator+"impl"+File.separator+"log4JConfigFile.xml";
    private final static String xsdPath = File.separator+"de"+File.separator+"neuland"+File.separator+"imgc"+File.separator+"command"+File.separator+"impl"+File.separator+"imageCrawlerReport.xsd";

    private static SchemaFactory schemaFactory = null; // expensive
    private static JAXBContext jAXBContext = null; // expensive


    private final BuildListener listener;
    private final String archiveDirSubPath;


    public CommandExecutor(final BuildListener listener, final String archiveDirSubPath) {
        if (listener == null) {
            throw new IllegalArgumentException(Messages.CommandExecutor_IllegalArgumentException_listener());
        }
        if (archiveDirSubPath == null || archiveDirSubPath.trim().equals("")) {
            throw new IllegalArgumentException(Messages.CommandExecutor_IllegalArgumentException_archiveDirSubPath());
        }
        this.listener = listener;
        this.archiveDirSubPath = archiveDirSubPath;
        if (schemaFactory == null) schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        if (jAXBContext == null) try {
            jAXBContext = JAXBContext.newInstance(ImageCrawlerReport.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e); // Should never happen
        }
    }

    @Override
    public boolean copyImageCrawler(final String executeDirectory, final String imgCPath){
        try {
            String target = executeDirectory+File.separator+getJarName(imgCPath);
            Files.copy(Paths.get(imgCPath), Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
            return new File(target).exists();
        } catch (IOException e) {
            listener.getLogger().println(Messages.CommandExecutor_isValidXML_Exception());
            e.printStackTrace(listener.getLogger());
            return false;
        }
    }

    @Override
    public boolean runImgCrawler(final String executeDir, final String imgCName, final List<String> imgCArgs) {
        final File tmpLog4JConfigFile = new File(executeDir + File.separator + "tmpLog4JConfigFile.xml");
        copyLog4JConfigInTmpLog4JConfigFile(tmpLog4JConfigFile);

        final String log4JConfig;
        if(!tmpLog4JConfigFile.exists() || tmpLog4JConfigFile.isDirectory()){
            listener.getLogger().println(Messages.CommandExecutor_couldNotCreateTmpLog4JConfigFile());
            log4JConfig = ""; // Should never happen
        }else{
            log4JConfig = "-Dlog4j.configurationFile=file:"+tmpLog4JConfigFile.getAbsoluteFile();
        }

        List<String> processArguments = createProcessArguments(log4JConfig,executeDir,imgCName);
        processArguments = addImgCArgsToProcessArguments(imgCArgs, processArguments);

        int result = executeImgC(executeDir,processArguments);
        if(!tmpLog4JConfigFile.delete()){
            listener.getLogger().println(Messages.CommandExecutor_canNotDeleteTmpLog4JConfigFile()); // Should never happen
        }
        return result == 0;
    }

    @Override
    public boolean saveImageCrawlerResultAsArtifact(final String executeDirectory, final String buildRootDir, final String absoluteFilePath) {
        final File resultFile = new File(absoluteFilePath);
        final File archiveDir = new File(buildRootDir,archiveDirSubPath);
        if(archiveDir.exists() && archiveDir.isFile()){
            listener.getLogger().println(Messages.CommandExecutor_saveImageCrawlerResultAsArtifact_invalid_archiveDir_isFile());
            return false;
        }
        if(!archiveDir.exists()){
            if(!archiveDir.mkdirs()){
                listener.getLogger().println(Messages.CommandExecutor_saveImageCrawlerResultAsArtifact_invalid_archiveDir_isFile());
                return false;
            }
        }
        final String source = resultFile.getAbsolutePath();
        final String target = archiveDir.getAbsolutePath().concat(File.separator).concat(resultFile.getName());
        try {
            Files.copy(Paths.get(source), Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
            return new File(target).exists();
        } catch (IOException e) {
            listener.getLogger().println(Messages.CommandExecutor_isValidXML_Exception());
            e.printStackTrace(listener.getLogger());
            return false;
        }
    }

    @Override
    public int analyseImageCrawlerResult(String xmlResult) throws InvalidXMLException{
        URL schemaFile = CommandExecutor.class.getResource(xsdPath);

        if(!isValidXML(xmlResult,schemaFile)) throw new InvalidXMLException();

        try {
            Unmarshaller jaxbUnmarshaller = jAXBContext.createUnmarshaller();
            ImageCrawlerReport imageCrawlerReport = (ImageCrawlerReport) jaxbUnmarshaller.unmarshal(new File(xmlResult));
            return imageCrawlerReport.getFailedImageCounter();
        } catch (JAXBException e) {
            listener.getLogger().println(Messages.CommandExecutor_isValidXML_Exception());
            e.printStackTrace(listener.getLogger());
            throw new InvalidXMLException();
        }

    }

    // package private for tests
    boolean isValidXML(String xmlResult,URL schemaFile){
        try {
            Source xmlFile = new StreamSource(new File(xmlResult));
            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            validator.validate(xmlFile);
            listener.getLogger().println(Messages.CommandExecutor_isValidXML());
            return true;
        } catch (IOException| SAXException e) {
            listener.getLogger().println(Messages.CommandExecutor_isValidXML_Exception());
            e.printStackTrace(listener.getLogger());
            return false;
        }
    }

    private void copyLog4JConfigInTmpLog4JConfigFile(File tmpLog4JConfigFile){
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            InputStreamReader isReader = new InputStreamReader(CommandExecutor.class.getResourceAsStream(log4JConfigResourcePath));
            br = new BufferedReader(isReader);
            bw = new BufferedWriter(new FileWriter(tmpLog4JConfigFile));
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e1) {
            listener.getLogger().println(Messages.CommandExecutor_isValidXML_Exception());
            e1.printStackTrace(listener.getLogger());
        } finally {
            try {
                if(br != null) br.close();
                if(bw != null) bw.close();
            }catch (IOException e2){}
        }
    }

    private int executeImgC(String executeDir, List<String> processArguments) {
        final ProcessBuilder pb = new ProcessBuilder(processArguments);
        pb.directory(new File(executeDir));
        try {
            final Process p = pb.start();
            final LoggingOutputStream error = new LoggingOutputStream(p.getErrorStream(), listener);
            final LoggingOutputStream output = new LoggingOutputStream(p.getInputStream(), listener);
            error.start();
            output.start();
            error.join(3000);
            output.join(3000);
            return p.waitFor();
        } catch (IOException | InterruptedException e) {
            listener.getLogger().println(Messages.CommandExecutor_isValidXML_Exception());
            e.printStackTrace(listener.getLogger());
            return -1;
        }
    }

    private String getJarName(String input){
        final String[] imgPath = input.split(File.separator);
        return (imgPath.length > 0) ? imgPath[imgPath.length-1] : input;
    }

    private List<String> addImgCArgsToProcessArguments(List<String> imgCArgs, List<String> processArguments) {
        for(String imgCArg : imgCArgs){
            processArguments.add(imgCArg);
        }
        return processArguments;
    }

    private List<String> createProcessArguments(String log4JConfig, String executeDir, String imgCName) {
        final List<String> processArguments = new ArrayList<>();
        processArguments.add("java");
        processArguments.add(log4JConfig);
        processArguments.add("-jar");
        processArguments.add(executeDir+File.separator+getJarName(imgCName));
        return processArguments;
    }

    // package-private for tests
    void setJAXBContext(JAXBContext jAXBContext){
        this.jAXBContext = jAXBContext;
    }
}
