package de.neuland.imgc.command.impl;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by jannikbohling on 31.03.15.
 */
public class CommandExecutorIT extends CommandExecutorTest{

    @Test
    public void shouldRunImgCrawlerAndCreateResultFilesIT() throws Exception {
        // given
        final String executeDir = RESOURCE_DIR;
        final String imgCName = IMG_C_NAME;
        final String cacheName = "Test.cache";
        final String resultName = "TestR";

        final List<String> imgCArgs = new ArrayList<>();
        imgCArgs.add("-c=example_config.config");
        imgCArgs.add("-cache="+cacheName);
        imgCArgs.add("-r="+resultName);
        try{
            // when
            final boolean r = c.runImgCrawler(executeDir,imgCName,imgCArgs);
            // then
            assertTrue(r);
            final File cache = new File(RESOURCE_DIR+File.separator+cacheName);
            assertTrue(cache.exists());
            assertTrue(cache.isFile());
            final File xmlResult = new File(RESOURCE_DIR+File.separator+resultName+".xml");
            assertTrue(xmlResult.exists());
            assertTrue(xmlResult.isFile());
            final File htmlResult = new File(RESOURCE_DIR+File.separator+resultName+".html");
            assertTrue(htmlResult.exists());
            assertTrue(htmlResult.isFile());
        } finally {
            // cleanUp
            final File cache = new File(RESOURCE_DIR+File.separator+cacheName);
            if(cache.exists()) cache.delete();
            final File xmlResult = new File(RESOURCE_DIR+File.separator+resultName+".xml");
            if(xmlResult.exists()) xmlResult.delete();
            final File htmlResult = new File(RESOURCE_DIR+File.separator+resultName+".html");
            if(htmlResult.exists()) htmlResult.delete();
        }
    }
}
