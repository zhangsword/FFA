Planning Poker Sample
=====================

Sample planning poker application using WebSockets. Allows real-time chat and planning poker-style voting. Also includes multiple rooms and a synchronized iframe so that everyone in the room can be looking at the same webpages at the same time.

## Running in Eclipse

1. Download and install [Eclipse with the WebSphere Developer Tools](https://developer.ibm.com/wasdev/downloads/liberty-profile-using-eclipse/).
2. Create a new Liberty Profile Server. See [step 3](https://developer.ibm.com/wasdev/downloads/liberty-profile-using-eclipse/) for details.
3. Clone this repository.
4. Import the sample into Eclipse using *File -> Import -> Maven -> Existing Maven Projects* option.
5. Create a new Liberty profile server. Make sure the server has the *websocket-1.0* feature enabled.
6. Deploy the sample into Liberty server. Right click on the *planningpoker* sample and select *Run As -> Run on Server* option. Find and select the Liberty profile server and press *Finish*. 
7. Go to: [http://localhost/PlanningPoker](http://localhost/PlanningPoker)

## Building

The sample can be build using [Apache Maven](http://maven.apache.org/).

```bash
$ mvn install
```

## Deploying to Bluemix

Click the button below to deploy your own copy of this application to [Bluemix](https://bluemix.net). Once the application is deployed, visit *http://&lt;yourAppName&gt;.mybluemix.net/PlanningPoker* to access the application. 

[![Deploy to Bluemix](https://bluemix.net/deploy/button.png)](https://bluemix.net/deploy?repository=https://github.com/WASdev/sample.planningpoker.git)

# Notice

© Copyright IBM Corporation 2014.

# License

```text
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
````
