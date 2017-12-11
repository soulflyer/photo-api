# photo-api

generated using Luminus version "2.9.11.62"

Provide system and database access for photo processing tasks. Used by the photo-front-end clojurescript app.

## Running

### Production
For routine use the server is started by running the uberjar with the command:
    java -Ddatabase-url="mongodb://127.0.0.1/photos" -jar ~/Code/Clojure/Luminus/photo-api/target/uberjar/photo-api.jar >> /tmp/mongo-log &
This starts the server on port 30001.
### Development
For development use, switch the client to port 30000 and fire up a clojure repl using M-x cider-jack-in and run (mount/start)
 
## API Details

Still in development so checkout the API docs page on http://localhost:31001/swagger-ui/index.html for the most recent details.

### api/projects

Gives a list of all photo projects.

### api/open/project/yyyy/mm/project

Open the specified project in the external viewer specified in the preferences table in the db.

### api/project/yyyy/mm/project

Return a list of all the pictures in the given project.


## License

Copyright © 2017 FIXME
