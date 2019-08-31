# thavalon-reborn
A rewrite of the THAvalon backend in Kotlin!

Includes a frontend written in React. 

## Project Structure
This project is designed to be run as a public api, and a disjoint frontend, which communicate through REST endpoints.
The api is located in the thavalon-api directory, and the default frontend is located in thavalon-frontend.

Heroku is used for project deployment, which means that to deploy the two projects to different urls, different git repositories must exist for each service. To accomplish this while maintaining the usability of a single repo, a git subtree structure is used. 

Two different complete environments are availiable: a staging environment located at qa.thavalon.com and qa.api.thavalon.com,
and a production environment located at thavalon.com and api.thavalon.com. 

To push to the subtrees for deployment, use ``push.py``, located in the scripts directory.

First push all changes to this repo.
Then,run:

``source venv/bin/activate`` to activate the python3 virtual environment.
then run ``python push.py [--deploy] [--production] [--section]>``

## How the backend works
All roles inherit from the base class Role (defined in Role.kt). Each role has a collection of information about the gamestate
that is given to the player with that role when the game is started. To populate this information, each role has a getUpdaters
method. This method returns a list of updaters, which consist of a function that modifies the game in some way and a priority
that says when the function should be applied relative to other updaters (higher priorities go first). When the game is created,
all updaters for all roles are collected, sorted, and applied in order to fill in all information for the current game. For the
exact details on how this works, see Role.kt and Game.kt.


