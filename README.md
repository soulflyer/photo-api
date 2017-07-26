# photo-api

generated using Luminus version "2.9.11.62"

Provide system and database access for photo processing tasks. Used by the photo-front-end clojurescript app.


## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run

## API Details

Still in development so checkout the API docs page on http://localhost:31000/swagger-ui/index.html for the most recent details.

### api/projects

Gives a list of all photo projects.

### api/open/project/yyyy/mm/project

Open the specified project in the external viewer specified in the preferences table in the db.

### api/project/yyyy/mm/project

Return a list of all the pictures in the given project.


## License

Copyright © 2017 FIXME
