package iCasa.dataset.manager.services;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public interface datasetManagerServices {
	/*
	 * The method sets the header for the dataset
	 * */
	public void setHeader(JSONObject snapshot, FileWriter csvWriter, BufferedReader br) throws IOException, JSONException;
	
	/*
	 * The method builds the header from a snapshot
	 * */
	public List<String> buildHeader(JSONObject snapshot) throws JSONException;
	
	/*
	 * The method builds the row from the snapshot
	 * */
	public List<String> buildRow(JSONObject snapshot) throws JSONException;
	
	/*
	 * The method writes a snapshot on the dataset
	 * */
	public void writeRow(List<String> row, FileWriter csvWriter) throws IOException;
	
	/*
	 * The method calls the methods of the interface in order to build the row and write it on the dataset
	 * */
	public void buildAndWrite(JSONObject snapshot) throws JSONException, IOException;
}
