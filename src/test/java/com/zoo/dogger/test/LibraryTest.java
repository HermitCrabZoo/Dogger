package com.zoo.dogger.test;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.zoo.base.Arrs;
import com.zoo.dogger.media.decoder.Decoder;
import com.zoo.dogger.media.decoder.KGDecoder;
import com.zoo.dogger.media.decoder.NEDecoder;

public class LibraryTest {

	public static void main(String[] args) {
//		new LibraryTest().convertKG();
		new LibraryTest().convertNE();
	}
	
	public void convertNE() {
		Decoder decoder = new NEDecoder();
		String[] fns= {"484614639-128-6ea11c2c4cc4cd7b9383c9f4112f05ba.uc","509512619-320-fc529c3301e6137169bd818a59a369b2.uc","18533983-320-f38240b532fe6645e73f692a67a67fb1.uc"};
		for (String fn:fns) {
			try (
					FileChannel inChannel = FileChannel.open(Paths.get("E:\\dog\\"+fn), StandardOpenOption.READ);
					FileChannel outChannel = FileChannel.open(Paths.get("E:\\dog\\"+fn+".mp3"), StandardOpenOption.WRITE,StandardOpenOption.READ, StandardOpenOption.CREATE);
					) {
				long skip=0,size=inChannel.size();
				MappedByteBuffer iBuffer=inChannel.map(MapMode.READ_ONLY, skip, size);
				MappedByteBuffer oBuffer=outChannel.map(MapMode.READ_WRITE, 0, size);
				//直接对缓冲区进行数据的读写操作
				byte[] raws = new byte[iBuffer.limit()];
				iBuffer.get(raws);
				byte[] rs=decoder.decode(raws);
				oBuffer.put(rs);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	public void convertKG() {
		Decoder decoder = new KGDecoder();
		String[] fns= {"00b40fbfb075fc7f8a8e21746cfd817f.kgtemp","edd6c2fa45933be470c8c0910b39c14b.kgtemp","731bdd69c735bd395fb88b3859706da7.kgtemp"};
		for (String fn:fns) {
			try (
					FileChannel inChannel = FileChannel.open(Paths.get("E:\\dog\\"+fn), StandardOpenOption.READ);
//					FileChannel outChannel = FileChannel.open(Paths.get("E:\\dog\\"+fn+".mp3"), StandardOpenOption.WRITE,StandardOpenOption.READ, StandardOpenOption.CREATE);
					) {
				int skip=44;
				MappedByteBuffer iBuffer=inChannel.map(MapMode.READ_ONLY, skip, 16);
//				MappedByteBuffer oBuffer=outChannel.map(MapMode.READ_WRITE, 0, 32);
				//直接对缓冲区进行数据的读写操作
				byte[] raws = new byte[iBuffer.limit()];
				iBuffer.get(raws);
				System.out.println(Arrs.join(",", raws));
				System.out.println(new String(raws, StandardCharsets.UTF_16));
				System.out.println(new String(raws, Charset.forName("GBK")));
				byte[] rs=decoder.decode(raws);
				System.out.println(Arrs.join(",", rs));
				System.out.println(new String(rs, StandardCharsets.UTF_16));
				System.out.println(new String(rs, Charset.forName("GBK")));
				System.out.println("====================================>>");
//				oBuffer.put(rs);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

	}

}
