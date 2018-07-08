package com.dima.img.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.dima.img.exception.NoImageException;
import com.dima.img.service.ResizingService;

@RestController
public class ImgController {
	
	public static final Logger logger = LoggerFactory.getLogger(ImgController.class);
	
	@Autowired
	private ResizingService resizer;
	
	@RequestMapping(value = "/thumbnail", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody byte[] index(	@RequestParam URL url, 
										@RequestParam int width, 
										@RequestParam int height) throws IOException {

		logger.debug("Requested scaling for URL : {}, to {}x{}", url.toExternalForm(), width, height);

		BufferedImage image = loadImage(url);		
		BufferedImage result = resizer.resize(image, width, height);
		return imageToBytes(result);
	}

	private byte[] imageToBytes(BufferedImage result) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){
			ImageIO.write(result, "jpg", baos);
			baos.flush();
			return baos.toByteArray();	
		}
	}

	BufferedImage loadImage(URL url) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<byte[]> response = restTemplate.getForEntity(url.toExternalForm(), byte[].class);			
			return ImageIO.read(new ByteArrayInputStream(response.getBody()));
		} catch (Exception e) {
			throw new NoImageException("Failed to load requested image: " + e.getMessage());
		}
	}

}