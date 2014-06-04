Application Version Detector
=====================================

## Feature List

* Query app version info timely (10 min in default)
* Manage app to be detector
* Manage subscriber
* Send mail to subscriber when app updates detected.
* User interface to query ? (optional?), if we must provide such interface, 
  than degrade the frequency of the first.


## Build And Deploy

This project is built with **Play Framework 2** (v2.2.2), you need to 
install play before build this project.
  
Here is a guide about installing play framework: [Installation](http://www.playframework.com/documentation/2.2.x/Installing).
You'd better download the _Play standalone distribution_.
  
After you set up the environment, get into the project, simply `play dist` will build and package the project.
  
### Run App
  
Unzip the distribution file, and get into the target dir:

`bash
nohup ./bin/app-version-detector &
`
  
## Data files

This app is using **H2** database(in embedded mode) to provide data persistence service.
The data base file is located in $APP_HOME/data. To backup the database, what you need to do
is simply copy the files in $APP_HOME/data/.

Logs are located in $APP_HOME/logs.

## Configuration

Configuration file is `$APP_HOME/conf/application.conf`.

This file contains lots of framework level configuration items, and also app level items. Here I'll
explain the App specific configurations. As for other detail conf, please ref 
[Play configuration](http://www.playframework.com/documentation/2.2.x/Configuration)

42 Matters API related configuration items: 

* `api.access_token` 42Matters Api access token 
* `api.lookup.url` 42 Matters Api query url
* `api.lookup.threshold` 42 Matters Api threshold (App use free license will have a 
  threshold on using the query api, it's 500/24hrs)
* `api.lookup.interval` interval (in minute) to do a query operation, default is 240 minutes (4 hours). 

Mail related configuration items:

* smtp.host="hzs-mbx2.corp.seven.com"
* smtp.port=25
* smtp.user="app-version-detector"
* smtp.password="ar909!!"

## API used to query app info

This app is using [42Matters](https://42matters.com/api/lookup) to query 
**Google Play Market** (since no official api provided by google) which has a restriction for free license.

More details please ref [Billing](https://42matters.com/api/billing).

You can find you api usage info from [API Usage](https://42matters.com/api/account)


