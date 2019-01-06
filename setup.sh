#!/bin/bash

#Setup File
#Run this setup script to set up environment variables as many tests/scripts
#depend upon them.
#
#Easiest thing to do is to throw it in your .bashrc or .bash_profile and run it 
#at startup.

#Sets environment variable STUDYSLUGDIR as the absolute path to StudySlug's
#directory.
STUDYSLUGDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
export STUDYSLUGDIR
