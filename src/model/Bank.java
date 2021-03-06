package model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import binarySearchTree.BinarySearchTree;
import hashtable.HashTable;
import heap.IHeap;
import queue.IQueue;
import stack.IStack;

/**
 * Represents the bank and its operations
 * @author usuario
 *
 */
public class Bank {
	
	private HashTable<String, Client> clientHashTable;
	private List<Client> clientList;
	private BinarySearchTree<String, Client> clientTree;
	private IQueue<Client> clientQueue;
	private IStack<Client> clientStack;
	private IHeap<Client> clientHeap;
	
	/**
	 * Builds a bank
	 */
	
	public Bank() {
		clientHashTable = new HashTable<String, Client>();
		clientList = new ArrayList<Client>();
		clientTree = new BinarySearchTree<String, Client>();
		clientQueue = new IQueue<Client>();
		clientStack = new IStack<Client>();
		clientHeap = new IHeap<Client>(100, true);
	}
	
	/**
	 * Fills the bank's data structures with new client data
	 * @param name
	 * @param iD
	 * @param cardNumber
	 * @param paymentDueDate
	 * @param memberSinceDate
	 * @throws Exception 
	 */
	
	public void fillClientData(String name, String iD, String cardNumber, LocalDate paymentDueDate,
							   LocalDate memberSinceDate, Account account, String priority, Double cardAmount) throws Exception {
		
		Client client = new Client(name, iD, cardNumber, paymentDueDate, memberSinceDate, account, priority, cardAmount);
		clientHashTable.insert(iD, client);
		clientList.add(client);
		clientTree.addNode(client.getId(), client);
		
		if(client.getPriority().equalsIgnoreCase(client.NORMAL)) 
			clientQueue.enqueue(client);
		else 
			clientHeap.insert(client);
	}
	
	/**
	 * Fills the bank's stack with canceled client data
	 * @param name
	 * @param iD
	 * @param cardNumber
	 * @param paymentDueDate
	 * @param memberSinceDate
	 * @throws Exception 
	 */
	
	public void fillCanceledClientData(String name, String iD, String cardNumber, LocalDate paymentDueDate, LocalDate memberSinceDate,
		Account account, String priority, Double cardAmount, LocalDate cancelationDate, String cancelationComments) throws Exception {
		
		Client client = new Client(name, iD, cardNumber, paymentDueDate, memberSinceDate, account, priority, cardAmount);
		client.getAccount().setCancelationDate(cancelationDate);
		client.getAccount().setCancelationComments(cancelationComments);
		clientStack.push(client);
	}
	
	/**
	 * Increases the amount of a client's savings account<br>
	 * <b>pre:</b> deposit>0 y cliente!=null<br>
	 * <b>post:</b> the amount of the client's savings account has increased by the value of amount<br>
	 * @param client
	 * @param deposit
	 */
	
	public void deposit(Client client, Double deposit) {
		Double amount = client.getAccount().getAmount()+deposit;
		client.getAccount().setAmount(amount);
	}
	
	/**
	 * Decreases the amount of a client's savings account<br>
	 * <b>pre:</b> deposit>0 y cliente!=null<br>
	 * <b>post:</b> the amount of the client's savings account has decreased by the value of withdrawal<br>
	 * @param client
	 * @param withdraw
	 * @throws Exception. If withdrawal is greater than the balance of the client's savings account
	 */
	
	public void withdraw(Client client, Double withdrawal) throws RuntimeException {
		
		Double amount = client.getAccount().getAmount()-withdrawal;
		
		if(amount<0)
			throw new RuntimeException("Invalid operation: Cannot withdraw an amount greater than the account balance.");
		else
			client.getAccount().setAmount(amount);
	}
	
	/**
	 * Pays a client's credit card amount spent so far using cash<br>
	 * <b>pre</b>: client!=null<br>
	 * <b>post:</b> client's credit card amount has been modified<br>
	 * @param client
	 * @param amount
	 * @return client's remaining cash
	 */
	
	public Double payCardAmount(Client client, Double amount) {
		
		Double cardAmount = client.getCardAmount()-amount;
		
		if(cardAmount>0)
			throw new RuntimeException("Insufficient amount: Please insert the total amount spent using the credit card.");
		else
			client.setCardAmount(0.0);
		return Math.abs(cardAmount);
	}
	
	/**
	 * Pays a client's credit card amount spent so far using his account balance<br>
	 * <b>pre</b>: client!=null<br>
	 * <b>post:</b> client's credit card amount and client's account balance have been modified<br>
	 * @param client
	 */
	
	public void payCardAmount(Client client) {
		
		Double cardAmount = client.getCardAmount()-client.getAccount().getAmount();
		
		if(cardAmount>0)
			throw new RuntimeException("Insufficient amount: Your account's balance is not enough.");
		else {
			client.setCardAmount(0.0);
			client.getAccount().setAmount(Math.abs(cardAmount));
		}
	}
	
	/**
	 * Cancels a client's savings account and deletes all his data from the data structures.
	 * Also incorporates the client data into a stack with extra information
	 * about his account<br>
	 * <b>pre:</b> client!=null<br>
	 * <b>post:</b> client is no longer contained in the bank list but in a exclusive stack<br>
	 * @param client
	 * @param cancelationDate
	 * @param cancelationComments
	 * @throws IOException 
	 */
	
	public void cancelAccount(Client client, LocalDate cancelationDate, String cancelationComments) {
		
		//Cancels a client's savings account as it adds values to cancelationDate and
		//cancelationComments. Client is stored in a stack so that it can be retrieved
		client.getAccount().setCancelationDate(cancelationDate);
		client.getAccount().setCancelationComments(cancelationComments);
		clientStack.push(client);

		//Deletes the client from the data structures in the bank
		clientList.remove(client);
		clientTree.deleteNode(client.getId());	
		clientHashTable.delete(client.getId());
	}
	
	/**
	 * Retrieves a client's savings account and adds all his data back into the data structures
	 * Also deletes the client from the canceled clients database
	 * <b>pre:</b> stack!=null<br>
	 * <b>post:</b> client is now part of the bank<br>
	 * @throws Exception 
	 */
	
	public void undo() throws Exception {
		
		//Retrieves a client's savings account as it adds null values to cancelationDate and
		//cancelationComments. Client is removed from the stack
		Client client = clientStack.pop();
		client.getAccount().setCancelationDate(null);
		client.getAccount().setCancelationComments(null);
		
		//Adds the client back into the data structures in the bank
		clientList.add(client);
		clientTree.addNode(client.getId(), client);
		clientHashTable.delete(client.getId());
	}
	
	/**
	 * Advances the normal client queue<br>
	 */
	
	public void next() {
		clientQueue.dequeue();
	}
	
	/**
	 * Advances the priority client queue<br>
	 */
	
	public void priorityNext() {
		clientHeap.extract();
	}

	/**
	 * Retrieves client data from the hashtable given a key<br>
	 * <b>pre:</b> client with key 'iD' exists<br>
	 * @param iD client key
	 * @return client data 
	 */
	
	public Client searchClient(String iD) { 
		return clientHashTable.get(iD);
	}
	
	/**
	 * Adapted from https://www.withexample.com/merge-sort-using-arraylist-java-example/<br>
	 * Sorts the client list by name using the mergesort algorithm<br>
	 * <b>pre:</b> client list is not empty<br>
	 * <b>post:</b> client list is sorted by name<br>
	 */
	
	public void sortClientsByName() {       
		divide(0, clientList.size()-1);
    }
	
	private void divide(int start,int end){
        
        //Divide till you breakdown your list to single element
        if(start<end && (end-start)>=1){
            int mid = (end + start)/2;
            divide(start, mid);
            divide(mid+1, end);        
            
            //merging Sorted array produce above into one sorted array
            merge(start,mid,end);            
        }       
    } 
	
	private void merge(int start,int mid,int end){
        
        //Below is the mergedarray that will be sorted array Array[i-mid] , Array[(mid+1)-end]
		List<Client> mergedSortedArray = new ArrayList<Client>();
        
        int left = start;
        int right = mid+1;
        
        while(left<=mid && right<=end){
            if(clientList.get(left).getName().compareTo(clientList.get(right).getName())<0){
                mergedSortedArray.add(clientList.get(left));
                left++;
            }else{
                mergedSortedArray.add(clientList.get(right));
                right++;
            }
        }       
        
        //Either of below while loop will execute
        while(left<=mid){
            mergedSortedArray.add(clientList.get(left));
            left++;
        }
        
        while(right<=end){
            mergedSortedArray.add(clientList.get(right));
            right++;
        }
        
        int i = 0;
        int j = start;
        //Setting sorted array to original one
        while(i<mergedSortedArray.size()){
            clientList.set(j, mergedSortedArray.get(i++));
            j++;
        }
    }
	
	/**
	 * Adapted from https://codereview.stackexchange.com/questions/181391/sorting-an-arraylist-of-vehicles-using-quick-sort<br>
	 * Sorts the client list by amount using the quicksort algorithm<br>
	 * <b>pre:</b> client list is not empty<br>
	 * <b>post:</b> client list is sorted by ID<br>
	 */
	
	public void sortClientsByAmount() {
		quickSort(0, clientList.size()-1);
	}

	private void quickSort(int a, int b) {
		
		if(a<b) { 
			
			Client pivot = clientList.get(b);
	
		    int left = a;
		    int right = b;
	
		    while (left < right) {
		    	
		    	while(clientList.get(left).getAccount().getAmount() < pivot.getAccount().getAmount())
		    		left++;
	
		        while(clientList.get(right).getAccount().getAmount() > pivot.getAccount().getAmount())
		            right--;
	
		        if (right > left) {
		            Collections.swap(clientList, left, right);
		        }
		    }
		    quickSort(a, right-1);
		    quickSort(right+1, b);
		}
	}
	
	/**
	 * Sorts the client list by the oldest using the insertion sort algorithm<br>
	 * <b>pre:</b> client list is not empty<br>
	 * <b>post:</b> client list is sorted by ID<br>
	 */
	
	public void sortClientsByTime() {
		
		for (int i = 1; i < clientList.size(); i++)  {
			
		    Client key = clientList.get(i);
		    int j = i-1;
		    
		    while (j >= 0 && key.getMemberSinceDate().compareTo(clientList.get(j).getMemberSinceDate()) < 0) {
		        clientList.set(j+1, clientList.get(j)); 
		        j--;
		    }
		    clientList.set(j+1, key);
		}
	}
	
	/**
	 * Sorts the client list by ID using the inorder traversal algorithm<br>
	 * <b>pre:</b> client list is not empty<br>
	 * <b>post:</b> client list is sorted by ID<br>
	 */
	
	public void sortClientsByID() {
		clientList.clear();
		clientTree.inOrder(clientList);
	}

	public List<Client> getClientList() {
		return clientList;
	}

	public IQueue<Client> getClientQueue() {
		return clientQueue;
	}

	public IStack<Client> getClientStack() {
		return clientStack;
	}

	public IHeap<Client> getClientHeap() {
		return clientHeap;
	}

	public void setHeap(IHeap<Client> clientHeap) {
		this.clientHeap = clientHeap;
	}
}
