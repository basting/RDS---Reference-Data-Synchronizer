# RDS - Reference Data Synchronizer
A utility which can help in synchronizing reference data between CRM and Billing.

Reference data in CRM is maintained as UDPLs and are maintained as Reference tables in Billing. 
For example: - Values in X_ADJUSTMENT_REASON UDP comes from ar1_credit_reason table & likewise many UDP's. 
So, what could be done is, for each level of UDPs could be mapped to various tables from which the values come from. 
This mapping can be achieved by defining a db link to the corresponding billing db & then specifying the table name, 
column names & conditions to retrieve values for the particular level of the UDP. 
Once this is done, the utility takes care to retrieve values from the corresponding columns from the particular 
table based on the conditions & update the UDP.

Alternatively, all these things can be configured in an XML files & 
can taken as input to the utility tool. The schema of XML file can also be formulated.

For eg: - X_ADJUSTMENT_REASON UDP, the level 2 values comes from ar1_credit_reason. 
So, the mapping could be done like below.

Table name :-     ar1_credit_reason
Column names:-    credit_reason_code - Level 2 Ref id 
                  credit_reason_description - Level 2 Title

Condition:-       manual_ind='Y' and Category_code='C'

Mapping to Parent Level (in this case Level 1):- 
Based on activity_level_code (A,C or I ? Account, Charge or Invoice respectively). The UDP looks like below.

Level 1 Value  Level 1 Ref_Id 	Level 2 Value	           Level 2 Ref_Id
Account	        A	        Account level Credit	     ACLCRD
Charge	        C	        Incorrect Charges	     INCCRG
Charge	        C	        Noisy Call	             BADCL
Invoice	        I	        Invoice Level Credit	     INLCRD
Invoice	        I	        Wrong Invoice Calculation    WROINV
Invoice	        I	        Adjust Invoice	             ADJINV
