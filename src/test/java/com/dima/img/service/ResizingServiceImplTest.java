package com.dima.img.service;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.BeforeClass;
import org.junit.Test;

public class ResizingServiceImplTest {
	
	static BufferedImage testImg = null;
	
	private final ResizingServiceImpl underTest = new ResizingServiceImpl();
	
	@BeforeClass
	public static void init() throws IOException {
		InputStream s = ResizingServiceImplTest.class.getClassLoader().getResourceAsStream("test_image.jpg");
		testImg = ImageIO.read(s);
	}
	
	@Test(expected=NullPointerException.class)
	public void testResizeBufferedImageIntInt_throwException_if_null() {
		underTest.resize(null, 100, 100);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testResizeBufferedImageIntInt_throwException_if_zero_dimentsion() {
		underTest.resize(testImg, 0, 0);
	}
	
	@Test
	public void testPad() {
		
		BufferedImage padded = underTest.pad(testImg, testImg.getWidth() +2, testImg.getHeight() +2);
		for(int i=0; i<padded.getWidth(); i++) {
			assertEquals(Color.black.getRGB(), padded.getRGB(i, 0));
			assertEquals(Color.black.getRGB(), padded.getRGB(i, padded.getHeight() -1));
		}

		for(int i=0; i<padded.getHeight(); i++) {
			assertEquals(Color.black.getRGB(), padded.getRGB(0, i));
			assertEquals(Color.black.getRGB(), padded.getRGB(padded.getWidth() -1, i));
		}

		assertEquals(testImg.getRGB(0, 0), padded.getRGB(1, 1));
	}

	@Test
	public void testResizeBufferedImageDimension_not_required() {		
		BufferedImage result = underTest.resize(testImg, new Dimension(1000, 1000));
		assertEquals(testImg.getWidth(), result.getWidth());
		assertEquals(testImg.getHeight(), result.getHeight());
	}

	@Test
	public void testScaleImage_correct() throws IOException {				
		BufferedImage actual = underTest.scaleImage(testImg, new Dimension(100, 200), 1d);
		assertEquals(actual.getWidth(), 100);
		assertEquals(actual.getHeight(), 200);
	}

	@Test
	public void testScaleDimention() {
		Dimension src = new Dimension(100, 200);
		double factor = 0.5;
		Dimension result = underTest.scaleDimention(src, factor);
		assertEquals(new Dimension(50, 100), result);
	}

	@Test
	public void testGetScaleFactor_oneDimension() {
		Dimension src = new Dimension(100, 100);
		Dimension dst = new Dimension(50, 200);
		double factor = underTest.getScaleFactor(src, dst);
		assertEquals(0.5d, factor, 0.000001);
	}

	@Test
	public void testGetScaleFactor_noScale() {
		Dimension src = new Dimension(100, 100);
		Dimension dst = new Dimension(110, 110);
		double factor = underTest.getScaleFactor(src, dst);
		assertEquals(1d, factor, 0.000001);
	}
	
	@Test
	public void testGetScaleFactor_sameSize() {
		Dimension src = new Dimension(100, 100);
		Dimension dst = new Dimension(100, 100);
		double factor = underTest.getScaleFactor(src, dst);
		assertEquals(1d, factor, 0.000001);
	}
}
