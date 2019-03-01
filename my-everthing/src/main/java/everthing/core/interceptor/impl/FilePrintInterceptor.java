package everthing.core.interceptor.impl;

import everthing.core.interceptor.FileInterceptor;

import java.io.File;

/**
 * 打印
 */
public class FilePrintInterceptor implements FileInterceptor {

    @Override
    public void apply(File file) {
        System.out.println(file.getAbsolutePath());
    }
}
