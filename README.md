# photo-api


Photo-API provides system and database access for photo processing tasks. Used by the hinh-anh clojurescript app.

## Installation
First install mongo. Currently this is the only database that works with photo-api. Although mongo and nosql seem to be falling out of favour due to some issues with possible data loss this particular use case hits none of the problems and mongo accessed via the monger library  remains the best fit by far. [Get mongo here](https://docs.mongodb.com/manual/administration/install-community/)

## Running

The server is started by running the uberjar with the command:

    java -Ddatabase-url="mongodb://127.0.0.1/photos" -jar ~/Code/Clojure/Luminus/photo-api/target/uberjar/photo-api.jar >> /tmp/mongo-log &

or on the cloud server:

    java -Ddatabase-url="mongodb+srv://soulflyer:<password>@soulflyer.qri2f.mongodb.net/photos?retryWrites=true&w=majority" -jar ~/Code/Clojure/photo-api/target/uberjar/photo-api.jar >> /tmp/mongo-log &
    
This starts the server on port 31001.
The second one needs to have the correct password where it says <password>.


### Development
For development use, I switch the client to port 31000 and fire up a clojure repl using M-x cider-jack-in and run (mount/start)

### Building

    lein uberjar

Should alter the title in the swagger configuration to make it easy to differentiate the development version from the production version.

## API Details

To see the full API, start the server and view the API docs page on http://localhost:31001/swagger-ui/index.html

## TODO
All calls to the API are currently implemented using GET. This needs to be switched over to PUT for the calls that actually change the database or cause external actions. Currently there are no actions that would cause a real problem by being repeated, but its not cool.

Also the parameter order is not consistent across all the calls.

## License

Copyright © 2017 Iain Wood
