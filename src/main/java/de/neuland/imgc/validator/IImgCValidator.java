package de.neuland.imgc.validator;

public interface IImgCValidator {

	boolean isValidExecuteDirectory(final String input);

	boolean isValidImgCPath(final String input);

    boolean isValidConfigurationFile(final String input);

    boolean isValidCacheFile(final String input);

    boolean isValidIgnoreDir(final String input);

    boolean isValidStartingWarning(final String input);

    boolean isValidStartingFail(final String input);
}
