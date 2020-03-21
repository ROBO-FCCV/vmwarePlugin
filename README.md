# Vmware-provider

Vmware-provider is a java project that can be used to access VMware vCenter.

## Preparing Tools

Obtain the open-source tool [Apache Maven](https://maven.apache.org) and [Apache Tomcat](https://tomcat.apache.org), used for compiling and packaging open-source software.

## Compilation and Packaging

 **Step 1**      Create a folder named **lib**.

 **Step 2**      Download software packages vCloud Suite SDK for Java 6.0 and vSphere Management SDK 6.5. The download path is https://code.vmware.com/sdks.

 **Step 3**      Decompress the packages and change some file names. The following table lists the mappings between original and new file names.

**Table 1-1** Mappings between original and new file names

| Software Package              | Original File Name                   | New File Name      |
| ----------------------------- | ------------------------------------ | ------------------ |
| vSphere Management SDK 6.5    | vim25.jar                            | vmware.jar         |
|                               | samples-core-1.0.0.jar               | core.jar           |
| vCloud Suite SDK for Java 6.0 | vapi-authentication-1.0.0.jar        | authentication.jar |
|                               | vapi-runtime-1.0.0.jar               | runtime.jar        |
|                               | vapi-samltoken-1.0.0.jar             | samltoken.jar      |
|                               | vcloudsuite-client-samples-6.0.0.jar | samples.jar        |
|                               | vcloudsuite-client-sdk-6.0.0.jar     | client.jar         |
|                               | vcloudsuite-lookupservice-6.0.0.jar  | lookupservice.jar  |

**Step 4**      Copy the renamed **jar** files to the **lib** folder. Although the names of the **wssamples.jar** file in the vSphere Management SDK 6.5, **ssoclient.jar** and **ssosamples.jar** files in the vCloud Suite SDK for Java 6.0 software package are not changed, copy them to the **lib** folder.

**Step 5**      Decompress the VMware source code package and copy the **lib** folder to the Vmware folder.

![1]()

![2]()

**Step 6**      Run the following command to compile and package the folder: mvn clean package.

![3]()

**Step 7**      If the "BUILD SUCCESS" message is displayed, the compilation and packaging are successful, and the **target** folder is automatically generated.

![4]()

![5]()

**Step 8**      Copy the **vmware-x.x.war** file in the **target** folder to **\tomcat_vmware\software**.

![6]()

![7]()

**Step 9**      Copy the Apache Tomcat installation package in Preparing Tools to **\tomcat_vmware\software**.

![8]()

**Step 10**    Compress the **tomcat_vmware** folder into the **tomcat_vmware.tar.gz** file.

![9]()

