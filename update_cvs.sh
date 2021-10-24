#!/bin/zsh

# THIS SCRIPT ONLY WORKS WITH ZSH

export CVS_RSH=ssh

CVS_FOLDER=../project

removed_files() {
	# does not detect CVS files (CVS and .settings folders)
	# excludes some files from the initial code (.project, .settings, .cproject)
	diff -r . $CVS_FOLDER | grep "Only in $SRC_CVS" \
		| sed "s/Only in $SRC_CVS\(.*\): /$SRC_CVS\1\//" \
		| grep -E '\.java$' \
}

# wrapper to update cvs quietly from the (git) repo root
cvs_update() {
	# run in subshell to keep cwd intact
	(cd $CVS_FOLDER && cvs -q update)
}

cp --parents **/*.java $CVS_FOLDER/

echo Changes to commit:
cvs_update
echo

# detect files that are not in git but are in CVS
if removed_files &>/dev/null; then
	echo "The following files have been deleted from Git but are still in CVS:"
	echo
	removed_files | xargs -n1 echo rm
  echo
fi

# detect files not added to CVS
if cvs_update |& grep -w '?' &>/dev/null; then
	echo "You have new files to add to CVS first:"
	echo
	cvs_update |& grep -w '?' | cut -d' ' -f2 | xargs -n1 echo cvs add
  echo
fi

echo Then, commit with
echo 'cvs commit -m "'"$(git log -1 --pretty="%B - From Git by %an at %ai")"'"'
