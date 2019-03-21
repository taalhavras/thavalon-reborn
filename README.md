# thavalon-reborn
A rewrite of the THAvalon backend in Kotlin!

## How to build/run
Requires jdk 1.8 or higher to build. Run `./build_react.sh` to build the frontend code, `./compile.sh` to build an executable jar
in `build/libs`, and `./run.sh` to start a local webserver on port 4444.

## How the backend works
All roles inherit from the base class Role (defined in Role.kt). Each role has a collection of information about the gamestate
that is given to the player with that role when the game is started. To populate this information, each role has a getUpdaters
method. This method returns a list of updaters, which consist of a function that modifies the game in some way and a priority
that says when the function should be applied relative to other updaters (higher priorities go first). When the game is created,
all updaters for all roles are collected, sorted, and applied in order to fill in all information for the current game. For the
exact details on how this works, see Role.kt and Game.kt.
