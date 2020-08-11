package com.naztech.email.api.service;



import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.naztech.email.api.dto.MailRequest;
import com.naztech.email.api.dto.MailResponse;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class EmailService {
	
	@Autowired
	private JavaMailSender sender;
	
	@Autowired
	private Configuration config;

	
	public MailResponse sendEmail(MailRequest request, Map<String, Object> model) throws Exception {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("DummyMailSend");

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.RED.getIndex());

		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		
	    FileOutputStream fileOut = new FileOutputStream("C:\\Users\\DELL\\Desktop\\Dotnet\\MailRe\\spring-boot-email-freemarker-master\\src\\main\\resources\\DummyMailSend.xlsx");
		workbook.write(fileOut);
		fileOut.close();
		
		//
		/* Properties properties = new Properties();

	      properties.put("mail.pop3.host", "smtp.gmail.com");
	      properties.put("mail.pop3.port", "587");
	      properties.put("mail.pop3.starttls.enable", "true");
	      Session emailSession = Session.getDefaultInstance(properties);
	  
	      //create the POP3 store object and connect with the pop server
	      Store store = emailSession.getStore("pop3s");

	      store.connect("smtp.gmail.com", "rokybulrayhanchowdhury@gmail.com", "amrjaantg");

	      //create the folder object and open it
	      Folder emailFolder = store.getFolder("INBOX");
	      emailFolder.open(Folder.READ_ONLY);

	      // retrieve the messages from the folder in an array and print it
	      Message[] messages = emailFolder.getMessages();
	      System.out.println("messages.length---" + messages.length);

	      for (int i = 0, n = messages.length; i < n; i++) {
	         Message message = messages[i];
	         System.out.println("---------------------------------");
	         System.out.println("Email Number " + (i + 1));
	         System.out.println("Subject: " + message.getSubject());
	         System.out.println("From: " + message.getFrom()[0]);
	         System.out.println("Text: " + message.getContent().toString());

	      }
	      */
		//
		//
		Properties properties = new Properties();

	      properties.put("mail.pop3.host", "pop.gmail.com");
	      properties.put("mail.store.protocol", "pop3");
	      properties.put("mail.pop3.port", "995");
	      properties.put("mail.pop3.starttls.enable", "true");
	      Session emailSession = Session.getDefaultInstance(properties);
	  
	      //create the POP3 store object and connect with the pop server
	      Store store = emailSession.getStore("pop3s");

	      store.connect("smtp.gmail.com", "rokybulrayhanchowdhury@gmail.com", "amrjaantg");

	      //create the folder object and open it
	      Folder emailFolder = store.getFolder("INBOX");
	      emailFolder.open(Folder.READ_ONLY);
	      BufferedReader reader = new BufferedReader(new InputStreamReader(
	    	      System.in));

	      // retrieve the messages from the folder in an array and print it
	       Message[] messages = emailFolder.getMessages();
	       System.out.println("messages.length---" + messages.length);
	      
	     

	         // close the store and folder objects
	         //emailFolder.close(false);
	         //store.close(); 
	         
	         
	         for (int i = 0; i < messages.length; i++) {
	            Message message = messages[i];
	            System.out.println("-----------------------------M---");
	            System.out.println(i +" "+messages[i]);
	            writePart(message);
	            /*String line = reader.readLine();
	            if ("YES".equals(line)) {
	               message.writeTo(System.out);
	            } else if ("QUIT".equals(line)) {
	               break;
	            }
	            */
	         }
	         
	        emailFolder.close(false);
	        store.close(); 
	         
	   

	     /* for (int i = 0, n = messages.length; i < n; i++) {
	         Message message = messages[i];
	         System.out.println("---------------------------------");
	         System.out.println("Email Number " + (i + 1));
	         System.out.println("Subject: " + message.getSubject());
	         System.out.println("From: " + message.getFrom()[0]);
	         System.out.println("Text: " + message.getContent().toString());

	      }
	      */
		  
		
		//
	
		
		
		MailResponse response = new MailResponse();
		MimeMessage message = sender.createMimeMessage();
		try {
			// set mediaType
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			// add attachment
			//helper.addAttachment("logo.png", new ClassPathResource("logo.png"));
			helper.addAttachment("DummyMailSend.xlsx", new ClassPathResource("DummyMailSend.xlsx"));

			Template t = config.getTemplate("email-template.ftl");
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

			helper.setTo(request.getTo());
			helper.setText(html, true);
			helper.setSubject(request.getSubject());
			helper.setFrom(request.getFrom());
			
			sender.send(message);

			response.setMessage("mail send to : " + request.getTo());
			response.setStatus(Boolean.TRUE);

		} catch (MessagingException | IOException | TemplateException e) {
			response.setMessage("Mail Sending failure : "+e.getMessage());
			response.setStatus(Boolean.FALSE);
		}

		return response;
	}


	      public void writePart(Part p) throws Exception {
	    	  int i=0;
	             if (p instanceof Message)
	                //Call methos writeEnvelope
	                writeEnvelope((Message) p);

	           /*  System.out.println("---------------------------k");
	             System.out.println("CONTENT-TYPE: " + p.getContentType());

	             //check if the content is plain text
	             if (p.isMimeType("text/plain")) {
	                System.out.println("This is plain text");
	                System.out.println("---------------------------");
	                System.out.println((String) p.getContent());
	             } 
	             //check if the content has attachment
	             else if (p.isMimeType("multipart/*")) {
	                System.out.println("This is a Multipart");
	                System.out.println("---------------------------");
	                Multipart mp = (Multipart) p.getContent();
	                int count = mp.getCount();
	                for (i = 0; i < count; i++)
	                   writePart(mp.getBodyPart(i));
	             } 
	             //check if the content is a nested message
	             else if (p.isMimeType("message/rfc822")) {
	                System.out.println("This is a Nested Message");
	                System.out.println("---------------------------");
	                writePart((Part) p.getContent());
	             } 
	             //check if the content is an inline image
	             else if (p.isMimeType("image/jpeg")) {
	                System.out.println("--------> image/jpeg");
	                Object o = p.getContent();

	                InputStream x = (InputStream) o;
	                // Construct the required byte array
	                System.out.println("x.length = " + x.available()); 
	                byte[] bArray = new byte[x.available()];
	                while ((i = (int) ((InputStream) x).available()) > 0) {
	                   int result = (int) (((InputStream) x).read(bArray));
	                   if (result == -1)
	                i = 0;
	               

	                   break;
	                }
	                FileOutputStream f2 = new FileOutputStream("/tmp/image.jpg");
	                f2.write(bArray);
	             } 
	             else if (p.getContentType().contains("image/")) {
	                System.out.println("content type" + p.getContentType());
	                File f = new File("image" + new Date().getTime() + ".jpg");
	                DataOutputStream output = new DataOutputStream(
	                   new BufferedOutputStream(new FileOutputStream(f)));
	                   com.sun.mail.util.BASE64DecoderStream test = 
	                        (com.sun.mail.util.BASE64DecoderStream) p
	                         .getContent();
	                byte[] buffer = new byte[1024];
	                int bytesRead;
	                while ((bytesRead = test.read(buffer)) != -1) {
	                   output.write(buffer, 0, bytesRead);
	                }
	             } 
	             else {
	                Object o = p.getContent();
	                if (o instanceof String) {
	                   System.out.println("This is a string");
	                   System.out.println("---------------------------");
	                   System.out.println((String) o);
	                } 
	                else if (o instanceof InputStream) {
	                   System.out.println("This is just an input stream");
	                   System.out.println("---------------------------");
	                   InputStream is = (InputStream) o;
	                   is = (InputStream) o;
	                   int c;
	                   while ((c = is.read()) != -1)
	                      System.out.write(c);
	                } 
	                else {
	                   System.out.println("This is an unknown type");
	                   System.out.println("---------------------------");
	                   System.out.println(o.toString());
	                }
	             }
	             */

	          }
	      public static void writeEnvelope(Message m) throws Exception {
	          System.out.println("This is the message envelope");
	          System.out.println("---------------------------");
	          Address[] a;

	          // FROM
	          if ((a = m.getFrom()) != null) {
	             for (int j = 0; j < a.length; j++)
	             System.out.println("FROM: " + a[j].toString());
	          }

	          // TO
	          if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
	             for (int j = 0; j < a.length; j++)
	             System.out.println("TO: " + a[j].toString());
	          }

	          // SUBJECT
	          if (m.getSubject() != null)
	             System.out.println("SUBJECT: " + m.getSubject());

	       }


	
	

}
