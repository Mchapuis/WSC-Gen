package ca.concordia.cse.gipsy.ws.soap;


import java.io.File;

import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;



import de.vs.unikassel.generator.gui.listener.TaskGenerator;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;




/**
 * Generator based on GeneratorGUIListener.java in de.vs.unikassel.generator.gui.listener
 * @author mrtnchps
 *
 */
@WebService(name="GeneratorWS")
public class GeneratorWS {

	public static boolean gipsy = false;                             
	private int numberOfConcepts = 10000;                            
	private int numberOfServices = 4000;                             
	private boolean solvableProblem = true;                          
	private int[] solutionDepths = null;                             
	private int[] solutionsList = null;                      		
	private int completeSolutionDepth = -1;                          
	private int minNumberConcepts = -1;  
	private String outputFolder = null;
	private String bpelFileName = null;
	private String owlFileName = null;
	private String taskWSDLFileName = null;
	private String WSLAFileName = null;
	private String serviceWSDLFileName = null;
	private boolean generateIntermediateFiles = false;
	private TaskGenerator taskGenerator = null;
	private String errorMessages = null;
	private boolean overrideFiles = true;
	private boolean ignoreMinimum = false;

	public GeneratorWS() {

	}


	/**
	 * Call this function to set default values for the Generator. 
	 * @see BrowseOutputFolder() also - need to be called
	 * @throws Exception
	 */
	@WebMethod(operationName="setDefault")
        public void setDefault() throws Exception{

		setNumberOfConcepts(10000);
		setNumberOfServices(4000);
		setSolvableProblem(true);
		solutionDepths = new int[1];
		solutionDepths[0] = 10;
		setSolutionsList(solutionDepths);
		setCompleteSolutionDepth();
		calculateMinNumberConcepts();
		setGipsy(false);
		setBpelFileName("Solution");
		setServiceWSDLFileName("Services");
		setOwlFileName("Taxonomy");
		setTaskWSDLFileName("Challenge");
		setWSLAFileName("Servicelevelagreements");
		setGenerateIntermediateFiles(false);
	}

	/**
	 * Called when all the elements for the generator is setup
	 * @see BrowseOutputFolder() also - this function need to be called or setup before start()
	 * @throws Exception
	 */
	@WebMethod(operationName="start")
        public void start() throws Exception{
		File outputFolderPath = new File(this.getOutputFolder());
		taskGenerator = new TaskGenerator(getNumberOfConcepts(), getSolvableProblem(), getSolutionsList(), getNumberOfServices(),
				outputFolderPath, getBpelFileName(), getServiceWSDLFileName(), getTaskWSDLFileName(), getOwlFileName(), getWSLAFileName(), getGenerateIntermediateFiles());
		Thread taskGeneratorThread = new Thread((Runnable) taskGenerator);
		taskGeneratorThread.start();
	}

	/**
	 * Default is 10 000 - This is the number of concepts the user wants
	 * 
	 * If the number is less than 0 - an error will be added in @param errorMessages
	 * @param numberOfConcepts
	 */
	@WebMethod(operationName="setNumberOfConcepts")
        public void setNumberOfConcepts(@WebParam(name= "numberOfConcepts") int numberOfConcepts) {
		if(numberOfConcepts <= 0) {
			// --- > exception could be replaced with an error message in JSP
			errorMessages+=("\n\tPlease enter a number greater than 0 for the number of concepts");
		}else{
			this.numberOfConcepts = numberOfConcepts;
		}
	}
	/**
	 * Getter for numberOfConcepts
	 * 
	 * @return numberOfConcepts
	 */
	@WebMethod(operationName="getNumberOfConcepts")
        public int getNumberOfConcepts(){
		return numberOfConcepts;
	}

	/**
	 * Setter for number of services
	 * 
	 * If the number is less than 0 - an error will be added in @param errorMessages
	 * @param numberOfServices
	 */
	@WebMethod(operationName="setNumberOfServices")
        public void setNumberOfServices(@WebParam(name= "numberOfServices")int numberOfServices){
		if(numberOfServices <=0){
			errorMessages+=("\n\tPlease enter a number greater than 0 for number of services");
		}else{
			this.numberOfServices = numberOfServices;
		}
	}

	/**
	 * Getter for numberOfServices
	 * @return
	 */
	@WebMethod(operationName="getNumberOfServices")
        public int getNumberOfServices(){
		return numberOfServices;
	}

	/**
	 * Setter for solvableProblem
	 * 
	 * If set to false, solutionDepth, completeSolutionDepth and minNumberOfConcepts are not usable
	 * @param solvableProblem is a boolean that enable the calculation of solutionDepths
	 */
	@WebMethod(operationName="setSolvableProblem")
        public void setSolvableProblem(@WebParam(name="solvableProblem")  boolean solvableProblem){
		this.solvableProblem = solvableProblem;
	}

	/**
	 *  Getter for solvableProblem
	 * @return solvableProblem
	 */
	@WebMethod(operationName="getSolvableProblem")
        public boolean getSolvableProblem(){
		return solvableProblem;
	}

	/**
	 * Getter for solutionsList
	 * @return solutionsList is an array of integer that represent solutionDepths asked by the user
	 */
	@WebMethod(operationName="getSolutionsList")
        public int[] getSolutionsList() {
		if(getSolvableProblem() == false){
			errorMessages+=("\n\tGet Solutions list available only if solvable problem set to True");
		}
		if (solutionsList == null) {
			solutionsList = new int[1];
			solutionsList[0]=10;
		}

		return solutionsList;
	}

	/**
	 * Handle the "Add solutions" button
	 * 
	 * If a user input many depth Numbers, it is added to the list
	 * @param depthNumbers is an array of integers bigger than 0
	 */
	@WebMethod(operationName="setSolutionsList")
        public void setSolutionsList(@WebParam(name="depthNumbers") int[] depthNumbers ) {
		int[] solutionsList;
		if(getSolvableProblem() == false){
			errorMessages+=("\n\tSet Solutions list available only if solvable problem set to True");
		}

		if(depthNumbers.length < 1){
			errorMessages+=("\n\tPlease enter a number bigger than 1 for a solution");
		}
		solutionsList = new int[depthNumbers.length];
		for(int i=0; i < depthNumbers.length; i++){
			solutionsList[i] =depthNumbers[i];
		}
		this.solutionsList = solutionsList;
	}

	/**
	 * Total solutionDepth number
	 * 
	 * @return completeSolutionDepth
	 */
        @WebMethod(operationName="getCompleteSolutionDepth")
	public int getCompleteSolutionDepth(){
		return this.completeSolutionDepth;
	}

	/**
	 * Sum the total solutionDepthList indexes to calculate the completeSolutionDepth
	 */
	@WebMethod(operationName="setCompleteSolutionDepth")
        public void setCompleteSolutionDepth(){
		int completeSolutionDepth = 0;
		if(getSolvableProblem() == false){
			errorMessages+=("\n\tComplete solution depth available only if solvable problem set to True");
		}
		for(int i=0; i < getSolutionsList().length; i++){
			completeSolutionDepth += getSolutionsList()[i];
		}
		this.completeSolutionDepth = completeSolutionDepth;
	}

	/**
	 * Setter for Gipsy
	 * @param _gipsy
	 */
	@WebMethod(operationName="setGipsy")
        public void setGipsy(@WebParam(name= "_gipsy")boolean _gipsy){
		gipsy = _gipsy;
	}

	/**
	 * Getter for Gipsy
	 * @return gipsy
	 */
	@WebMethod(operationName="getGispy")
        public boolean getGispy(){
		return gipsy;
	}

	/**
	 * Set the folder path to save the files
	 * @param outputFolderPath
	 */
	@WebMethod(operationName="setOutputFolder")
        public void setOutputFolder(@WebParam(name="outputFolderPath")String outputFolderPath) {
		if(outputFolderPath == null || outputFolderPath.trim().equals("")){
			errorMessages+=("\n\tPlease enter a valid name for the output path");
		}

		// file
		File outputFolder = new File(outputFolderPath);
		if(!outputFolder.exists()) {
			errorMessages+=("\n\tPath doesn't exist or is incorrect for the output folder");

		}

		if(!outputFolder.isDirectory()) {
			errorMessages+=("\n\tFolder must be a directory");

		}
		this.outputFolder = outputFolderPath;
	}

	@WebMethod(operationName="getOutputFolder")
        public String getOutputFolder(){
		return outputFolder;
	}

	/**
	 * Refactored function from GeneratorGUIListerner.java
	 * 
	 * Returns "true" if the directory contains the given file.
	 * @param directory The directory.
	 * @param fileName The name of the file.
	 * @return "true" if the directory contains the given file otherwise "false".
	 */
        @WebMethod(operationName="containsFile")
	public boolean containsFile(@WebParam(name="outputFolderPath")String outputFolderPath, 
                                    @WebParam(name= "fileName") String fileName) {
		File directory = new File(outputFolderPath);
		if(!directory.isDirectory()) {
			return false;
		}

		File[] files = directory.listFiles();
		for(File file : files) {
			if(file.getName().equals(fileName)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Refactored function from GeneratorGUIListener.java
	 * 
	 * Setter for Bpel file name
	 * 
	 * 1- Need to be a valid name.
	 * 2- Adding .bpel at the end of the name if it doesn't have it
	 * 
	 * @param bpelFileName
	 * @throws Exception if the user didn't check the UI box to override a file with the same name
	 */
	@WebMethod(operationName="setBpelFileName")
        public void setBpelFileName(@WebParam(name= "bpelFileName")String bpelFileName) throws Exception {
		if(bpelFileName == null || bpelFileName.trim().equals("")){
			throw new Exception("Bpel file name must be a valid name");
		}
		if(!bpelFileName.endsWith(".bpel")){
			bpelFileName = bpelFileName+".bpel";
		}
		if(containsFile(getOutputFolder(), bpelFileName) && !getOverrideFiles()){
			throw new Exception("Files with same name already exist: bpel. Please check box to override or change directory");

		}else{
			// --------------- should ask the user if it is ok to override files
			this.bpelFileName = bpelFileName;	
		}

	}

	/**
	 * 
	 * @return bpel file name
	 * @throws Exception if file name is null or empty
	 */
        @WebMethod(operationName="getBpelFileName")
	public String getBpelFileName() throws Exception{
		if(bpelFileName == null || bpelFileName.trim().equals("")){
			throw new Exception("bpel file name must be valid");
		}
		return bpelFileName;
	}

	/**
	 * Setter for overridingFiles
	 * @param override will allow overriding files with same names in directory
	 */
	@WebMethod(operationName="setOverrideFiles")
        public void setOverrideFiles(@WebParam(name="override") boolean override){
		this.overrideFiles = override;
	}

	/**
	 * Getter for overriding files
	 * @return overrideFiles
	 */
	@WebMethod(operationName="getOverrideFiles")
        public boolean getOverrideFiles(){
		return overrideFiles;
	}

	/**
	 * Setter for OwlFileName
	 * @param owlFileName
	 * @throws Exception if file name is null or empty, also if the user didn't ask to override and the operation will override files
	 */
	@WebMethod(operationName="setOwlFileName")
        public void setOwlFileName(@WebParam(name="owlFileName") String owlFileName) throws Exception{
		if(owlFileName == null || owlFileName.trim().equals("")){
			throw new Exception("Owl file name must be valid");
		}
		if(!owlFileName.endsWith(".owl")){
			owlFileName = owlFileName+".owl";
		}
		// Check if the file already exists.
		if(containsFile(getOutputFolder(), owlFileName) && !getOverrideFiles()) {
			throw new Exception("Files with same name already exist: owl. Please check box to override or change directory");

		}else{
			this.owlFileName = owlFileName;
		}

	}
	/**
	 * Getter for OwlFileName
	 * @return the owlfilename
	 * @throws Exception if the file is null or empty
	 */
	@WebMethod(operationName="getOwlFileName")
        public String getOwlFileName() throws Exception{
		if(owlFileName == null || owlFileName.trim().equals("")){
			throw new Exception("owl file name must be valid");
		}
		return owlFileName;
	}

	/**
	 * Setter for taskWSDLFileName
	 * @param taskWSDLFileName
	 * @throws Exception if file is null or empty, also if the user didn't ask to override and the operation will override files
	 */
	@WebMethod(operationName="setTaskWSDLFileName")
        public void setTaskWSDLFileName(@WebParam(name="taskWSDLFileName") String taskWSDLFileName) throws Exception{
		if(taskWSDLFileName == null || taskWSDLFileName.trim().equals("")){
			throw new Exception("task-wsdl-file must have a valid name");
		}
		// Correct file-extension.
		if(!taskWSDLFileName.endsWith(".wsdl")) {
			taskWSDLFileName = taskWSDLFileName+".wsdl";
		}
		// Check if the file already exists.
		if(containsFile(outputFolder, taskWSDLFileName) && !getOverrideFiles()) {

			throw new Exception("Files with same name already exist: taskWsdlFileName. Please check box to override or change directory");

		}else{
			this.taskWSDLFileName = taskWSDLFileName;
		}

	}

	/**
	 *  Getter
	 * @return taskWSDLFileName
	 * @throws Exception if the file name is empty or null
	 */
	@WebMethod(operationName="getTaskWSDLFileName")
        public String getTaskWSDLFileName() throws Exception{
		if(taskWSDLFileName == null || taskWSDLFileName.trim().equals("")){
			throw new Exception("taskWSDLFileName file name must be valid");
		}
		return this.taskWSDLFileName;
	}

	/**
	 * Refactored function from GeneratorGUIListener.java
	 * 
	 * Setter for WSLAFileName
	 * @param WSLAFileName
	 * @throws Exception if empty or null string, also if file will be override and the user didn't ask for it
	 */
	@WebMethod(operationName="setWSLAFileName")
        public void setWSLAFileName(@WebParam(name="WSLAFileName") String WSLAFileName) throws Exception{
		if(WSLAFileName == null || WSLAFileName.trim().equals("")) {
			throw new Exception("WSLA file name must be valid");

		}

		// Correct file-extension.
		if(!WSLAFileName.endsWith(".wsla")) {
			WSLAFileName = WSLAFileName+".wsla";
		}
		if(containsFile(outputFolder, WSLAFileName) && !getOverrideFiles()) {
			throw new Exception("Files with same name already exist: WSLAFileName. Please check box to override or change directory");

		}else{
			this.WSLAFileName = WSLAFileName;
		}
	}


	/**
	 * Getter
	 * @return WSLAFileName
	 * @throws Exception if string is  null or empty
	 */
	@WebMethod(operationName="getWSLAFileName")
        public String getWSLAFileName() throws Exception{
		if(WSLAFileName == null || WSLAFileName.trim().equals("")){
			throw new Exception("WSLAFileName file name must be valid");
		}
		return this.WSLAFileName;
	}

	/**
	 * Setter
	 * @param serviceWSDLFileName
	 * @throws Exception if file is null or empty, also if the file will be override and the user didn't ask for it
	 */
	@WebMethod(operationName="setServiceWSDLFileName")
        public void setServiceWSDLFileName(@WebParam(name="serviceWSDLFileName")String serviceWSDLFileName) throws Exception{

		if(serviceWSDLFileName == null || serviceWSDLFileName.trim().equals("")) {
			throw new Exception("service wsdl file must be valid");
		}
		// Correct file-extension.
		if(!serviceWSDLFileName.endsWith(".wsdl")) {
			serviceWSDLFileName = serviceWSDLFileName+".wsdl";
		}
		// Check if the file already exists.
		if(containsFile(outputFolder, serviceWSDLFileName) && !getOverrideFiles()) {
			throw new Exception("Files with same name already exist: serviceWSDLFileName. Please check box to override or change directory");
		}else{
			this.serviceWSDLFileName = serviceWSDLFileName;
		}

	}

	/**
	 * Getter
	 * @return serviceWSDLFileName
	 * @throws Exception
	 */
	@WebMethod(operationName="getServiceWSDLFileName")
        public String getServiceWSDLFileName() throws Exception{
		if(serviceWSDLFileName == null || serviceWSDLFileName.trim().equals("")){
			throw new Exception("serviceWSDLFileName file name must be valid");
		}
		return this.serviceWSDLFileName;
	}

	/**
	 * User can decide if intermediateFiles need to be created or not
	 * 
	 * Setter
	 * @param generateIntermediateFiles
	 */
	@WebMethod(operationName="setGenerateIntermediateFiles")
        public void setGenerateIntermediateFiles(@WebParam(name= "generateIntermediateFiles")boolean generateIntermediateFiles){
		this.generateIntermediateFiles = generateIntermediateFiles;
	}

	/**
	 * Getter
	 * @return generateIntermediateFiles
	 */
	
        @WebMethod(operationName="getGenerateIntermediateFiles")
        public boolean getGenerateIntermediateFiles(){
		return this.generateIntermediateFiles;
	}

	/**
	 * User can decide if it wants to ignore the calculated minimum based on the solutiondepths numbers
	 * 
	 * @param ignoreMinimum
	 */
	@WebMethod(operationName="setIgnoreMinimum")
        public void setIgnoreMinimum(@WebParam(name= "ignoreMinimum")boolean ignoreMinimum){
		this.ignoreMinimum = ignoreMinimum;
	}

	/**
	 * Getter
	 * @return ignoreMinimum
	 */
	@WebMethod(operationName="getIgnoreMinimum")
        public boolean getIgnoreMinimum(){
		return ignoreMinimum;
	}

	/**
	 * Calculate the minimum number concepts 
	 */
        @WebMethod(operationName="calculateMinNumberConcepts")
	public void calculateMinNumberConcepts() {
		int depth = getCompleteSolutionDepth();
		minNumberConcepts = depth * depth;

		if(getNumberOfConcepts() < minNumberConcepts && !getIgnoreMinimum()){		
			errorMessages+=("\n\tNumber of Concepts should be at least "+minNumberConcepts+" based on the added values of solution...Please check the box if you want to ignore");
		}
	}

	/**
	 * Browse folders....Refactored method from GeneratorGUIListener.java
	 */
        @WebMethod(operationName="browseOutputFolder")
	public void browseOutputFolder(){
		// Create a file-chooser.
		JFileChooser fileChooser=new JFileChooser();

		// Restrict the selection to directories.
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// Set the file-filter. Only directories are shown.
		fileChooser.setFileFilter(new FileFilter() {	

			@Override
			public boolean accept(File file) {
				if(file.isDirectory()) {
					return true;
				}
				else {
					return false;
				}
			}

			@Override
			public String getDescription() {
				return "Folders";					
			}
		});

		// Display the file-chooser.
		int option = fileChooser.showOpenDialog(fileChooser);

		// Check the return-status.
		if(option == JFileChooser.APPROVE_OPTION) {
			String pathOutputFolder = fileChooser.getSelectedFile().getAbsolutePath();
			setOutputFolder(pathOutputFolder);
		}
	}


	@WebMethod(operationName="getErrorMessages")
        public String getErrorMessages(){
		return this.errorMessages;
	}

}
