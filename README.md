SimpleDHT
=========

Simple Key Value Storage:-
Simple Key value storage is based on a chord protocol. 
Chord is a distributed look up protocol that addresses the problem of distributed hashing that is it maps the keys onto a node. 
Chord adapts efficiently as nodes join and leave the system, and can answer queries even if the system is continuously changing. The Chord protocol supports just one operation: given a key, it maps the key onto a node. Depending on the application using Chord, that node might be responsible for storing a value associated with the key.

Components:-
  Content provider: Used to store all the messages in the form of keys and values and provide an interface to insert and query the keys. 
	Server thread: It continuously listens for all the messages coming from different AVD’s. 
	Client thread: It is used to send the message from one AVD to another. 
	Parser: It is used to recognize the message came from another AVD and execute appropriate action based on the message received.
	Insert function: It is used to insert the value to the appropriate AVD based on the hash value generated from the genHash function.
	Query function:  It is used query the key stored on the appropriate AVD.
These are the main components used in my design.
There are also GUI components that are as follows:-
	LDump : This button is used to display all the local key value pairs on the AVD it is clicked on.
	GDump : This button displays all the key value pairs stored on the chord ring.
	OnTestClickListener : This button is used to test the content provider.

Architecture of the chord ring:
genHash(“5556”) < genHash(“5554”) < genHash(“5558”)
So node 5556 will be first node in the ring and then node 5554 and lastly node 5558. All the node joins are handled by node 5554. Along with the successor and predecessor pointers, node position is also maintained with every node.

Algorithm:-
	Firstly AVD 5554 joins in the chord ring and the hash value of the avd 5554 is generated based on string “5554”. Its successors and predecessors pointers are updated as 11108 and 11108 respectively since it’s the only node in the ring.
	Afterwards suppose AVD 5556 joins. Now the hash(“5556”) < hash(“5554”) so AVD 5556 becomes predecessor of node 5554 and the pointers become as 
AVD 5554 predecessor – 11112 successor – 11112
AVD 5556 predecessor – 11108 successor – 11108
	Now finally AVD 5558 joins. Now Hash(“5556”) < Hash(“5554”) < Hash(“5558”), so the node ring becomes as follows :
AVD 5554 predecessor – 11112 successor – 11116
AVD 5556 predecessor – 11116 successor – 11108
AVD 5558 predecessor – 11108 successor – 11112
	Now the value can be inserted in the chord ring.  


Pseudo code:-
Insert pseudo code:-
if(keyhash.compareTo(node_id) <=0 ) {
	if(suc_pointer== 11108 && pre_pointer ==11108 && port_avd==5554) {   // only 5554 is present
		/* insert */
	}
	else if(pos==1) {      // key is less than the smallest node
		/* insert*/ 
	}
	else if(keyhash.compareTo(pre_hash)>0) {	// found the perfect position
		/* insert */
	}
	else {
		/* pass on to successor */
	}
else {
	if(keyhash.compareTo(pre_hash)>0 && pos.equals("1")) {   // value is greater than every node in ring
		/* insert */
	else {
		/* pass on to successor */
	}
 }
 
Query Pseudo code:-
if(keyhash.compareTo(node_id)<=0 && keyhash.compareTo(pre_hash)>0) {	
	/* value found locally */
}
else if(keyhash.compareTo(pre_hash)>0 && pos.equals("1")) {
	/* value found on first avd and value greater than every node */
}
else if(keyhash.compareTo(node_id)<=0 && pos.equals("1")) {
	/* again value found on 1st AVD */
}
else if(suc_pointer== 11108 && pre_pointer ==11108 && port_avd==5554) {
	/* only one node in the ring and value on that AVD */
}
else {
	/* Query pass on to successor */
}


Conclusion :-
The target of this assignment was to learn how to design the distributed key value storage based on chord and to insert and query those stored keys. The design of the Chord protocol is completed and fully understood.

References:-
•	http://developer.android.com/training/index.html
•	http://conferences.sigcomm.org/sigcomm/2001/p12-stoica.pdf



