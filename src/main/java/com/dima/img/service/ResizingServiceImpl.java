package com.dima.img.service;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ResizingServiceImpl implements ResizingService {

	public static final Logger logger = LoggerFactory.getLogger(ResizingServiceImpl.class);
			
	@Override
	public BufferedImage resize(BufferedImage image, int width, int height) {
		if (width <=0 || height <=0) {
			throw new IllegalArgumentException("Target image size should be > 0");
		}
		BufferedImage resized = resize(Objects.requireNonNull(image), new Dimension(width, height));
		BufferedImage result = resized;
		if (resized.getWidth() < width || resized.getHeight() < height) {
			result = pad(resized, width, height);
		}
		
		return result;
	}

	BufferedImage pad(BufferedImage src, int width, int height) {
		logger.debug("Padding image to {}x{}", width, height);		

		BufferedImage padded = new BufferedImage(width, height, src.getType());

		Graphics g = padded.getGraphics();

		g.setColor(Color.black);
		g.fillRect(0, 0, padded.getWidth(), padded.getHeight());
		g.drawImage(src, (width - src.getWidth()) / 2, (height - src.getHeight()) / 2, null);
		g.dispose();
		return padded;
	}
	
	BufferedImage resize(BufferedImage img, Dimension target) {
		Dimension src = new Dimension(img.getWidth(), img.getHeight());
		double factor = getScaleFactor(src, target);
		Dimension dst = scaleDimention(src, factor);
		return (factor == 1) ? img : scaleImage(img, dst, factor);
	}

	BufferedImage scaleImage(BufferedImage srcImage, Dimension dst, double factor) {
		BufferedImage result = null;
		if (srcImage != null) {
			logger.debug("Resizing original image to {}x{}", dst.width, dst.height);			
			result = new BufferedImage(dst.width, dst.height, srcImage.getType());
			Graphics2D g = result.createGraphics();
			AffineTransform at = AffineTransform.getScaleInstance(factor, factor);
			g.drawRenderedImage(srcImage, at);
		}
		return result;
	}

	Dimension scaleDimention(Dimension src, double factor) {
		return new Dimension(
				(int) Math.round((double) src.width * factor),
				(int) Math.round((double) src.height * factor));
	}

	double getScaleFactor(Dimension original, Dimension target) {
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
