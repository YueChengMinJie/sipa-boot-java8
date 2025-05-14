package com.sipa.boot.java8.common.dtos;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class ZipFile {
    private String fileName;

    private byte[] data;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public static ZipEntityBuilder builder() {
        return new ZipEntityBuilder();
    }

    public static final class ZipEntityBuilder {
        private String fileName;

        private byte[] data;

        private ZipEntityBuilder() {}

        public ZipEntityBuilder withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public ZipEntityBuilder withData(byte[] data) {
            this.data = data;
            return this;
        }

        public ZipFile build() {
            ZipFile zipFile = new ZipFile();
            zipFile.setFileName(fileName);
            zipFile.setData(data);
            return zipFile;
        }
    }
}
