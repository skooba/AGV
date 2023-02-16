package com.mochilafulfillment.server.api;

import com.mochilafulfillment.server.controls_logic.dtos.PathRecord;

import java.io.*;
import java.util.LinkedList;

// Public storage object for LinkedList<PathRecord>
// This is the public API for modifying the path
public class PickPath {
    private LinkedList<PathRecord> list;

    public PickPath(String csvFilePath) throws IOException {
        PathRecordsUpload uploadPathRecord = new PathRecordsUpload();
        ClassLoader classLoader = getClass().getClassLoader();
        InputStreamReader inputStreamReader = new InputStreamReader(classLoader.getResourceAsStream(csvFilePath));
        list = uploadPathRecord.fromCsv(inputStreamReader);
    }

    public PathRecord getNext() {
        PathRecord nextRecord = list.pollFirst();
        return nextRecord;
    }

    public PathRecord peekNext() {
        PathRecord nextRecord = list.peekFirst();
        return nextRecord;
    }

    public class PathRecordsUpload {

        public LinkedList<PathRecord> fromCsv(InputStreamReader inputStreamReader) throws IOException {
            LinkedList<PathRecord> recordList = new LinkedList<>();
            String line = "";
            String csvSplitBy = ",";

            BufferedReader br = new BufferedReader(inputStreamReader);  // To read .csv in JAR need to use this method, Also .csv file has to be in src.main.resources for Maven to compile automatically.
            // using comma as separator takes data from csv and saves to variables forming path record
            // each path record is saved to the record list
            // each line in the CSV is a new path record
            while ((line = br.readLine()) != null) {
                String[] tags = line.split(csvSplitBy);
                int tagId = Integer.parseInt(tags[0]);
//                System.out.println(tagId);
                int rotateByDegrees = Integer.parseInt(tags[1]);
                int pickType = Integer.parseInt(tags[2]);
                PathRecord record = new PathRecord(tagId, rotateByDegrees, pickType);
                recordList.add(record);
            }
            return(recordList);
        }
    }

}







