# Feezal
This is my first larger Scala project. I'm attempting here to implement a (really simple) voice interface between human and a computer.

## Libraries
So far I'm using Sphinx 4 for speech recognition and FreeTTS for computer voice. For logging is used util-logging from twitter guys.

## Development notes
I'm trying to go with a modular architecture - dynamic loading of modules is already working. I like the functional programming, but I'm still pretty new to it so my code is probably in state of a mix of procedural and functional approach. I'd be glad for any criticism (preferably constructive one) - this project should serve (me) to learn more about functional programming and Scala.

## Current state
The core is working = audio input, audio output and modules. I'm beginning work on modules itself, it's possible (and probable) that the module interface will change.
