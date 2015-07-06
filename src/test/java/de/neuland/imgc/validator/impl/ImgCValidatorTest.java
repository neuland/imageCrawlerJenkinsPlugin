package de.neuland.imgc.validator.impl;

import de.neuland.imgc.validator.impl.ImgCValidator;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ImgCValidatorTest {

    private ImgCValidator v = new ImgCValidator();

    @Test
    public void shouldBeValidExecuteDirectory() {
        // given
        final String validExecuteDirectory = System.getProperty("user.dir");
        // when
        final boolean result = v.isValidExecuteDirectory(validExecuteDirectory);
        // then
        assertTrue(result);
    }

    @Test
    public void shouldBeInvalidExecuteDirectoryIfDirectoryDoesNotExist() {
        // given
        final String nonExistingDirectory = System.getProperty("user.dir")+new Timestamp(new Date().getTime());
        // when
        final boolean result = v.isValidExecuteDirectory(nonExistingDirectory);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidExecuteDirectoryIfNameIsEmpty() {
        // given
        final String emptyName = " ";
        // when
        final boolean result = v.isValidExecuteDirectory(emptyName);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidExecuteDirectoryIfNameIsNull() {
        // given
        final String emptyName = null;
        // when
        final boolean result = v.isValidExecuteDirectory(emptyName);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidExecuteDirectoryIfNameIsInvalid() {
        // given
        final String invalidName = getInvalidChars()+"Test";
        // when
        final boolean result = v.isValidExecuteDirectory(invalidName);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeValidImgCPath() throws IOException {
        // given
        final String validImgCPath = System.getProperty("user.dir") + File.separator + "ImgC.jar";
        try{
            // create given stuff
            new File(validImgCPath).createNewFile();
            // when
            final boolean result = v.isValidImgCPath(validImgCPath);
            // then
            assertTrue(result);
        } finally {
            // cleanUp
            final File imgC = new File(validImgCPath);
            if(imgC.exists()) imgC.delete();
        }
    }

    @Test
    public void shouldBeInvalidImgCPathIfNameIsEmpty() {
        // given
        final String emptyName = " ";
        // when
        final boolean result = v.isValidImgCPath(emptyName);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidImgCPathIfNameIsNull() {
        // given
        final String emptyName = null;
        // when
        final boolean result = v.isValidImgCPath(emptyName);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidImgCPathIfItIsNotAJarFile() throws IOException {
        // given
        final String validImgCPath = System.getProperty("user.dir") + File.separator + "ImgC.txt";
        try{
            // create given stuff
            new File(validImgCPath).createNewFile();
            // when
            final boolean result = v.isValidImgCPath(validImgCPath);
            // then
            assertFalse(result);
        } finally {
            // cleanUp
            final File imgC = new File(validImgCPath);
            if(imgC.exists()) imgC.delete();
        }
    }

    @Test
    public void shouldBeInvalidImgCPathIfNameIsInvalid() {
        // given
        final String invalidName =  getInvalidChars()+"test.jar";
        // when
        final boolean result = v.isValidImgCPath(invalidName);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidImgCPathIfJarDoesNotExist() {
        // given
        final String nonExistingJar = System.getProperty("user.dir")+ File.separator + new Timestamp(new Date().getTime()) +".jar";
        // when
        final boolean result = v.isValidImgCPath(nonExistingJar);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeValidConfigurationFile() throws IOException {
        // given
        final String configurationFileName = System.getProperty("user.dir") + File.separator + "c.config";
        try{
            // create given stuff
            new File(configurationFileName).createNewFile();
            // when
            final boolean result = v.isValidConfigurationFile(configurationFileName);
            // then
            assertTrue(result);
        } finally {
            // cleanUp
            final File configurationFile = new File(configurationFileName);
            if(configurationFile.exists()) configurationFile.delete();
        }
    }

    @Test
    public void shouldBeInvalidConfigurationFileIfNameIsEmpty() {
        // given
        final String emptyName = " ";
        // when
        final boolean result = v.isValidConfigurationFile(emptyName);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidConfigurationFileIfNameIsNull() {
        // given
        final String emptyName = null;
        // when
        final boolean result = v.isValidConfigurationFile(emptyName);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidConfigurationFileIfNameIsInvalid() {
        // given
        final String invalidName =  getInvalidChars()+"test.config";
        // when
        final boolean result = v.isValidConfigurationFile(invalidName);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidConfigurationFileIfFileDoesNotExist() {
        // given
        final String nonExistingFile = System.getProperty("user.dir")+ File.separator + new Timestamp(new Date().getTime()) +".config";
        // when
        final boolean result = v.isValidConfigurationFile(nonExistingFile);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeValidCacheFile() throws IOException {
        // given
        final String cacheFileName = System.getProperty("user.dir") + File.separator + "c.cache";
        try{
            // create given stuff
            new File(cacheFileName).createNewFile();
            // when
            final boolean result = v.isValidCacheFile(cacheFileName);
            // then
            assertTrue(result);
        } finally {
            // cleanUp
            final File cacheFile = new File(cacheFileName);
            if(cacheFile.exists()) cacheFile.delete();
        }
    }

    @Test
    public void shouldBeValidCacheFileIfNameIsEmpty() {
        // given
        final String emptyName = " ";
        // when
        final boolean result = v.isValidCacheFile(emptyName);
        // then
        assertTrue(result);
    }

    @Test
    public void shouldBeValidCacheFileIfNameIsNull() {
        // given
        final String emptyName = null;
        // when
        final boolean result = v.isValidCacheFile(emptyName);
        // then
        assertTrue(result);
    }

    @Test
    public void shouldBeInvalidCacheFileIfNameIsInvalid() {
        // given
        final String invalidName =  getInvalidChars()+"test.cache";
        // when
        final boolean result = v.isValidCacheFile(invalidName);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidCacheFileIfItIsNotACacheFile() throws IOException {
        // given
        final String cacheFileName = System.getProperty("user.dir") + File.separator + "c.cacxhe";
        try{
            // create given stuff
            new File(cacheFileName).createNewFile();
            // when
            final boolean result = v.isValidCacheFile(cacheFileName);
            // then
            assertFalse(result);
        } finally {
            // cleanUp
            final File cacheFile = new File(cacheFileName);
            if(cacheFile.exists()) cacheFile.delete();
        }
    }

    @Test
    public void shouldBeInvalidCacheFileIfItIsADirectory() throws IOException {
        // given
        final String cacheFileName = System.getProperty("user.dir") + File.separator + "c.cache";
        try{
            // create given stuff
            new File(cacheFileName).mkdirs();
            // when
            final boolean result = v.isValidCacheFile(cacheFileName);
            // then
            assertFalse(result);
        } finally {
            // cleanUp
            final File cacheFile = new File(cacheFileName);
            if(cacheFile.exists()) cacheFile.delete();
        }
    }

    @Test
    public void shouldBeValidIgnoreDir() throws IOException {
        // given
        final String ignoreDirName = System.getProperty("user.dir");
        try{
            // when
            final boolean result = v.isValidIgnoreDir(ignoreDirName);
            // then
            assertTrue(result);
        } finally {
            // cleanUp
            final File ignoreDir = new File(ignoreDirName);
            if(ignoreDir.exists()) ignoreDir.delete();
        }
    }

    @Test
    public void shouldBeInvalidIgnoreDirIfNameIsEmpty() {
        // given
        final String emptyName = " ";
        // when
        final boolean result = v.isValidIgnoreDir(emptyName);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidIgnoreDirIfNameIsNull() {
        // given
        final String emptyName = null;
        // when
        final boolean result = v.isValidIgnoreDir(emptyName);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidIgnoreDirIfNameIsInvalid() {
        // given
        final String invalidName =  getInvalidChars()+System.getProperty("user.dir");
        // when
        final boolean result = v.isValidIgnoreDir(invalidName);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidIgnoreDirIfItIsAFile() throws IOException {
        // given
        final String ignoreDirFileName = System.getProperty("user.dir") + File.separator + "ignoreFile";
        try{
            // create given stuff
            new File(ignoreDirFileName).createNewFile();
            // when
            final boolean result = v.isValidIgnoreDir(ignoreDirFileName);
            // then
            assertFalse(result);
        } finally {
            // cleanUp
            final File ignoreDirFile = new File(ignoreDirFileName);
            if(ignoreDirFile.exists()) ignoreDirFile.delete();
        }
    }

    @Test
    public void shouldBeValidStartingWarningIfInputNumberIsAPositiveNumber() {
        // given
        final int min = 1;
        final int max = 10000000;
        final String inputNumber = ""+new Random().nextInt((max - min) + 1) + min;
        // when
        final boolean result = v.isValidStartingWarning(inputNumber);
        // then
        assertTrue(result);
    }

    @Test
    public void shouldBeValidStartingWarningIfInputNumberIsOne() {
        // given
        final String inputNumber = "1";
        // when
        final boolean result = v.isValidStartingWarning(inputNumber);
        // then
        assertTrue(result);
    }

    @Test
    public void shouldBeValidStartingWarningIfInputNumberIsMinusOne() {
        // given
        final String inputNumber = "-1";
        // when
        final boolean result = v.isValidStartingWarning(inputNumber);
        // then
        assertTrue(result);
    }

    @Test
    public void shouldBeInvalidStartingWarningIfInputNumberIsNotANumber() {
        // given
        final String inputNumber = "A";
        // when
        final boolean result = v.isValidStartingWarning(inputNumber);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidStartingWarningIfInputNumberIsNull() {
        // given
        final String inputNumber = null;
        // when
        final boolean result = v.isValidStartingWarning(inputNumber);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidStartingWarningIfInputNumberIsEmpty() {
        // given
        final String inputNumber = " ";
        // when
        final boolean result = v.isValidStartingWarning(inputNumber);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidStartingWarningIfInputNumberIsZero() {
        // given
        final String inputNumber = "0";
        // when
        final boolean result = v.isValidStartingWarning(inputNumber);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidStartingWarningIfInputNumberIsANegativeNumber() {
        // given
        final int min = 2;
        final int max = 10000000;
        final String inputNumber = ""+((new Random().nextInt((max - min) + 1) + min)*-1);
        // when
        final boolean result = v.isValidStartingWarning(inputNumber);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeValidStartingFailIfInputNumberIsAPositiveNumber() {
        // given
        final int min = 1;
        final int max = 10000000;
        final String inputNumber = ""+new Random().nextInt((max - min) + 1) + min;
        // when
        final boolean result = v.isValidStartingFail(inputNumber);
        // then
        assertTrue(result);
    }

    @Test
    public void shouldBeValidStartingFailIfInputNumberIsOne() {
        // given
        final String inputNumber = "1";
        // when
        final boolean result = v.isValidStartingFail(inputNumber);
        // then
        assertTrue(result);
    }

    @Test
    public void shouldBeValidStartingFailIfInputNumberIsMinusOne() {
        // given
        final String inputNumber = "-1";
        // when
        final boolean result = v.isValidStartingFail(inputNumber);
        // then
        assertTrue(result);
    }

    @Test
    public void shouldBeInvalidStartingFailIfInputNumberIsNotANumber() {
        // given
        final String inputNumber = "A";
        // when
        final boolean result = v.isValidStartingFail(inputNumber);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidStartingFailIfInputNumberIsNull() {
        // given
        final String inputNumber = null;
        // when
        final boolean result = v.isValidStartingFail(inputNumber);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidStartingFailIfInputNumberIsEmpty() {
        // given
        final String inputNumber = " ";
        // when
        final boolean result = v.isValidStartingFail(inputNumber);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidStartingFailIfInputNumberIsZero() {
        // given
        final String inputNumber = "0";
        // when
        final boolean result = v.isValidStartingFail(inputNumber);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldBeInvalidStartingFailIfInputNumberIsANegativeNumber() {
        // given
        final int min = 2;
        final int max = 10000000;
        final String inputNumber = ""+((new Random().nextInt((max - min) + 1) + min)*-1);
        // when
        final boolean result = v.isValidStartingFail(inputNumber);
        // then
        assertFalse(result);
    }

    // isValidStartingWarning
    // - empty 0, < -1

    private String getInvalidChars(){
        final String OS = System.getProperty("os.name").toLowerCase();
        if (OS.indexOf("win") >= 0) {
             return "\\/:*?\"<>|";
        } else if (OS.indexOf("mac") >= 0) {
            return "\u0000";
        } else { // assume Unix/Linux
            return "/";
        }
    }

}
