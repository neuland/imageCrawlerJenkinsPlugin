package de.neuland.imgc.validator.impl;

import java.io.File;
import java.io.IOException;

import de.neuland.imgc.validator.IImgCValidator;

public class ImgCValidator implements IImgCValidator{

    @Override
	public boolean isValidExecuteDirectory(final String input) {
        return isNotEmpty(input) && isValidFilename(input) && isExistingDirectory(input);
	}

    @Override
	public boolean isValidImgCPath(final String input){
		return isNotEmpty(input) && isJarFile(input) && isValidFilename(input) && isExistingFile(input);
	}

    @Override
    public boolean isValidConfigurationFile(final String input) {
        return isNotEmpty(input) && isValidFilename(input) && isExistingFile(input);
    }

    @Override
    public boolean isValidCacheFile(final String input) {
        return isEmpty(input) || isValidFilename(input) && isCacheFile(input) && isNotADirectory(input);
    }

    @Override
    public boolean isValidIgnoreDir(final String input) {
        return isNotEmpty(input) && isValidFilename(input) && isNotAFile(input);
    }

    @Override
    public boolean isValidStartingWarning(final String input) {
        return isNotEmptyAndAPositiveNumberOrMinusOne(input);
    }

    @Override
    public boolean isValidStartingFail(final String input) {
        return isNotEmptyAndAPositiveNumberOrMinusOne(input);
    }

    private boolean isNotEmptyAndAPositiveNumberOrMinusOne(String input){ return isNotEmpty(input) && isPositiveOrMinusOne(input);}

    private boolean isNotADirectory(String input) {
        return !new File(input).isDirectory();
    }
	
	private boolean isJarFile(final String input){
		return input.endsWith(".jar");
	}

    private boolean isCacheFile(final String input){
        return input.endsWith(".cache");
    }

	private boolean isExistingDirectory(final String input){
		final File f = new File(input);
    	return f.exists() && f.isDirectory();
	}
	
	private boolean isExistingFile(final String input){
		final File f = new File(input);
    	return f.exists() && f.isFile();
	}

    private boolean isNotAFile(String input) {
        return !new File(input).isFile();
    }

    private boolean isValidFilename(final String input) {
        final File f = new File(input);
        try {
            final String r = f.getCanonicalPath();
            return isNotEmpty(r);
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isNotEmpty(final String input){
        return isNotNull(input) && input.trim().length() > 0;
    }

    private boolean isEmpty(String input) { return !isNotEmpty(input);}

    private boolean isPositiveOrMinusOne(final String input){
        if(input.matches("-?\\d++")){
            final int i = Integer.valueOf(input);
            return i == -1 || i > 0;
        }
        return false;
    }

    private boolean isNull(Object input){
        return input == null;
    }

    private boolean isNotNull(Object input){
        return !isNull(input);
    }
}