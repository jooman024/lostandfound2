package icia.js.lostandfound.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class openCVConfig {
	static {
    	nu.pattern.OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }
}
