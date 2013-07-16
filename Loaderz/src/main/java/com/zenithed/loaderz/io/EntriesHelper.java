package com.zenithed.loaderz.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.ContentValues;
import android.content.Context;

import com.zenithed.loaderz.provider.FeedsContract;

public class EntriesHelper extends OperationHelper {

	public EntriesHelper(Context context) {
		super(context);
	}

	@Override
	public ContentValues [] parse(String reponseString) throws IOException {
		
		final ArrayList<ContentValues> batch = new ArrayList<ContentValues>();
		InputStream inputStream = new ByteArrayInputStream(reponseString.getBytes());
		
		try {
			
			final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Element docEle = db.parse(inputStream).getDocumentElement();

	        // Get a list of each stackoverflow entry.
	        NodeList entries = docEle.getElementsByTagName("entry");
	        if (entries != null && entries.getLength() > 0) {
				
	        	Element entry, id, title, link, published, updated, summery;
				String idString = null;
				ContentValues values;
				
				
				final int SIZE = entries.getLength();
				for (int i = 0; i < SIZE; i++) {
					entry = (Element) entries.item(i);
					id = (Element) entry.getElementsByTagName("id").item(0);
					title = (Element) entry.getElementsByTagName(FeedsContract.Entry.TITLE).item(0);
					link = (Element) entry.getElementsByTagName(FeedsContract.Entry.LINK).item(0);
					published = (Element) entry.getElementsByTagName(FeedsContract.Entry.PUBLISHED).item(0);
					updated = (Element) entry.getElementsByTagName(FeedsContract.Entry.UPDATED).item(0);
					summery = (Element) entry.getElementsByTagName(FeedsContract.Entry.SUMMARY).item(0);
					
					idString = id.getFirstChild().getNodeValue();
					
					values = new ContentValues();
					
					values.put(FeedsContract.Entry._ID, idString.substring(idString.lastIndexOf("/") + 1));
					values.put(FeedsContract.Entry.TITLE, title.getFirstChild().getNodeValue());
					values.put(FeedsContract.Entry.LINK, link.getAttribute("href"));
					values.put(FeedsContract.Entry.PUBLISHED, published.getFirstChild().getNodeValue());
					values.put(FeedsContract.Entry.UPDATED, updated.getFirstChild().getNodeValue());
					values.put(FeedsContract.Entry.SUMMARY, summery.getFirstChild().getNodeValue());
					
					batch.add(values);
				}
			}
	        
	        return (ContentValues[]) batch.toArray(new ContentValues [batch.size()]);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new IOException("Parsing error.");
			
		} catch (SAXException e) {
			e.printStackTrace();
			throw new IOException("Parsing error.");
			
		} finally {
			inputStream.close();
		}
	}

}
