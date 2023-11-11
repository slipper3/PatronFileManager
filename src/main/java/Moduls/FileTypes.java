package Moduls;

import java.util.ArrayList;
import java.util.Hashtable;

public class FileTypes {
    public Hashtable<String, ArrayList<String>> hashtable = new Hashtable<>();

    public FileTypes() {
        ArrayList<String> applications = new ArrayList<>();
        applications.add("exe");

        ArrayList<String> archives = new ArrayList<>();
        archives.add("zip");
        archives.add("rar");

        ArrayList<String> documents = new ArrayList<>();
        documents.add("pdf");
        documents.add("docx");
        documents.add("doc");
        documents.add("xlsx");
        documents.add("csv");
        documents.add("xls");
        documents.add("txt");
        documents.add("odt");
        documents.add("ods");
        documents.add("ppt");
        documents.add("pptx");
        documents.add("tif");
        documents.add("tiff");
        documents.add("bmp");
        documents.add("gif");
        documents.add("eps");
        documents.add("cr2");
        documents.add("nef");
        documents.add("orf");
        documents.add("sr2");

        ArrayList<String> images = new ArrayList<>();
        images.add("png");
        images.add("jpg");
        images.add("jpeg");

        ArrayList<String> music = new ArrayList<>();
        music.add("mp3");
        music.add("m4a");
        music.add("flac");
        music.add("wav");
        music.add("wma");
        music.add("aac");

        ArrayList<String> videos = new ArrayList<>();
        videos.add("mp4");
        videos.add("webm");
        videos.add("mpg");
        videos.add("mp2");
        videos.add("mpeg");
        videos.add("mpe");
        videos.add("mpv");
        videos.add("ogg");
        videos.add("m4p");
        videos.add("m4v");
        videos.add("avi");
        videos.add("wmv");
        videos.add("mov");
        videos.add("qt");
        videos.add("flv");
        videos.add("swf");
        videos.add("avchd");
        videos.add("mkv");

        hashtable.put("App", applications);
        hashtable.put("Archives", archives);
        hashtable.put("Documents", documents);
        hashtable.put("Images", images);
        hashtable.put("Music", music);
        hashtable.put("Videos", videos);

    }
}
