---
layout: post
title: The Pumpernickel Project
---

# Description #

The broadest explanation of this project is: this is a collection of free (as in beer) classes I've developed over the years. Some are related to work projects, and some are related to personal projects.

## History ##

Before 2017 this code was hosted at [javagraphics.java.net](https://javagraphics.java.net/) with an accompanying blog at [javagraphics.blogspot.com](https://javagraphics.blogspot.com/). These classes used the package "com.bric", but this (the pumpernickel) project uses "com.pump".

Over the years I strayed further and further from exclusively writing about graphics and Swing components. In this project I switched to an abstract moniker ("pumpernickel") to comfortably develop in multiple domains.

## Organization ##

This project is broken up into over a dozen clusters of subprojects. This diagram shows this subprojects and their dependencies:

![Chart of Pumpernickel subprojects](https://raw.githubusercontent.com/mickleness/pumpernickel/master/docs/Pumpernickel-Organization.png "Organization Chart")

## Showcase ##

So having read all of the above: you still probably don't really know what this project is about. The [Showcase application](https://github.com/mickleness/pumpernickel/tree/master/pump-release/com/pump/pump-showcase) is the simplest way to jump in and see the widgets and libraries in action.

In the previous com.bric incarnation of this project: I wrote several dozen blog articles, and most of those articles included a small accompanying stand-alone app. In this project: my plan is to try throwing most everything I can into the Showcase app. (If you see something in the repo that isn't represented in the Showcase app yet: it's probably on my to-do list!)

## Maven ##

If you look around the repository you'll see all these pom.xml files, and you'll notice that each subproject is named after an artifact ID. So this project uses Maven, right?

No. Or at least "not yet". I'm still inexperienced with Maven, and I had trouble setting this up as as Maven project. Instead I wrote a custom tool that builds my jars (while respecting the artifact IDs and versions) that should roughly mimic how Maven would build things, but I probably missed a few spots. I hope to get around to this someday. On top of all the other things I hope to get around to someday.