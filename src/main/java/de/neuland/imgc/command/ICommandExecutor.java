package de.neuland.imgc.command;

import java.util.List;

public interface ICommandExecutor {

    boolean copyImageCrawler(final String executeDirectory, final String imgCPath);

    boolean runImgCrawler(final String executeDir, final String imgCName, final List<String> imgCArgs);

    boolean saveImageCrawlerResultAsArtifact(final String executeDirectory, final String buildRootDir, final String absoluteFilePath);

    int analyseImageCrawlerResult(final String xmlResult) throws InvalidXMLException;
}
