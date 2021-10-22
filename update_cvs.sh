#!/bin/zsh

# THIS SCRIPT ONLY WORKS WITH ZSH

CVS_FOLDER=../project

removed_files() {
	# does not detect CVS files (CVS and .settings folders)
	# excludes some files from the initial code (.project, .settings, .cproject)
	diff -r . $CVS_FOLDER | grep "Only in $SRC_CVS" \
		| sed "s/Only in $SRC_CVS\(.*\): /$SRC_CVS\1\//" \
		| grep -E '\.java$' \
}

cp --parents **/*.java $CVS_FOLDER/

echo "The following files have been deleted from Git but are still in CVS:"
removed_files

