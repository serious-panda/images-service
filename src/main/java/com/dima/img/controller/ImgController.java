package com.dima.img.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.dima.img.exception.NoImageException;

@RestController
public class ImgController {
	
	public static final Logger logger = LoggerFactory.getLogger(ImgController.class);
	
//	@ExceptionHandler({ Exception.class })
//	public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest request) {
//	    ApiError apiError = new ApiError(
//	      HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), "error occurred");
//	    return new ResponseEntity<ApiError>(
//	      apiError, new HttpHeaders(), apiError.getStatus());
//	}

	
	@RequestMapping(value = "/thumbnail", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody byte[] index(@RequestParam("url") URL url, @RequestParam int width, @RequestParam int height) throws IOException {

		BufferedImage image = null;
		
		try {
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<byte[]> response = restTemplate.getForEntity(url.toExternalForm(), byte[].class);
	
			image = ImageIO.read(new ByteArrayInputStream(response.getBody()));
		} catch (Exception e) {
			throw new NoImageException("Failed to load requested image: " + e.getMessage());
		}
		
		BufferedImage result = resizeAndPad(image, width, height);

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){
			ImageIO.write(result, "jpg", baos);
			baos.flush();
			return baos.toByteArray();	
		}
	}

	private BufferedImage resizeAndPad(BufferedImage image, int width, int height) {
		BufferedImage resized = resize(image, new Dimension(width, height));

		BufferedImage newImage = new BufferedImage(width, height, image.getType());

		Graphics g = newImage.getGraphics();

		g.setColor(Color.red);
		g.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
		g.drawImage(resized, (width - resized.getWidth()) / 2, (height - resized.getHeight()) / 2, null);
		g.dispose();
		return newImage;
	}

	private BufferedImage resize(BufferedImage img, Dimension target) {
		Dimension src = new Dimension(img.getWidth(), img.getHeight());
		double factor = getScaleFactor(src, target);
		Dimension dst = scaleDimention(src, factor);
		return (factor == 1) ? img : scaleImage(img, dst, factor);
	}

	private BufferedImage scaleImage(BufferedImage sbi, Dimension dst, double factor) {
		BufferedImage dbi = null;
		if (sbi != null) {
			dbi = new BufferedImage(dst.width, dst.height, sbi.getType());
			Graphics2D g = dbi.createGraphics();
			AffineTransform at = AffineTransform.getScaleInstance(factor, factor);
			g.drawRenderedImage(sbi, at);
		}
		return dbi;
	}

	private Dimension scaleDimention(Dimension src, double factor) {
		return new Dimension((int) Math.round((double) src.width * factor),
				(int) Math.round((double) src.height * factor));
	}

	private double getScaleFactor(Dimension original, Dimension target) {
		double fsctor = 1d;

		if (original != null && target != null) {
			double widthFactor = (double) target.width / (double) original.width;
			double heightFactor = (double) target.height / (double) original.height;
			fsctor = Math.min(widthFactor, heightFactor);
			fsctor = Math.min(fsctor, 1); // no scale up
		}
		return fsctor;
	}
}