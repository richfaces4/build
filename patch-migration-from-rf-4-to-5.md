Patch Migration from RF 4.3 to RF 5.0
=====================================

The migration of patches from RichFaces 4 to 5  is not straight-forward, because:

* the RF 4 repositories were merged as subtrees of RF 5
* the RF 5 sources got unified line-endings using dos2unix
* the sources files were renamed

The patches can't be applied directly, but they can be cleverly changed to apply cleanly,
depending on complexity of the patch - the source code changes usually applies cleanly.

There are set of steps and utilities which can help you to port a patch from RF 4.3 to RF 5.

Note: following process helps to migrate batch of patches - in case of having few low-complexity patches, it's better to migrate them manually.

Patch formatting
----------------

Use `git format-patch` to export commits in patch file format in for given range of commits.

Following command will generate patches in scope of 4.3.0.Final and 4.3.1.Final tags specifically
for components repository:

    cd richfaces4/components/
    mkdir -p ~/patches/components/
    git format-patch 4.3.0.20130130-Final..4.3.1.20130305-Final -o ~/patches/components/

Note: you should manually remove maintanance patches, e.g. change of plugin versions, commits bumping version for release, etc.

Convert all patch files to UNIX line endings
--------------------------------------------

    cd ~/patches/components/
    dos2unix *

This command will unify line endings, so the patches should apply cleanly for specific file changes.

Detect specific changes
-----------------------

The only change needed in case that nothing changed between the source in RF 4 and RF 5
is detect where is the new file located. We can automatically deal with following changes:

Start with setting up what repository commits we are migrating and step into richfaces5 source tree:

    export PATCHES=~/patches/components/
    cd richfaces5/

Moved files
-----------

The source file has moved and it has only one occurence in the source tree.

We can generate `sed` to change patch files safely.

    for original in `cat $PATCHES/*  | egrep '^(---|\+\+\+) ' | egrep -v '\/dev\/null'  | sed -r 's#(---|\+\+\+) (a|b)/(([^ ]+)/([^\/]+))$#\3#g' | uniq`; do basename=`basename $original`; count=`find -name $basename | wc -l`; if [[ "$count" -eq 1 ]]; then target=`find -name $basename | sed -r 's#^\./##'`; echo "sed -ri 's#$original#$target#g' *"; fi; done

The command generates the list of `set` commands which can be then copied to clipboard and executed on `richfaces5` repository - expected script's output:

    cd $PATCHES
    sed -ri 's#input/ui/src/test/integration/org/richfaces/component/select/TestSelectKeyboardSelection.java#framework/src/test/integration/org/richfaces/component/select/TestSelectKeyboardSelection.java#g' *
    sed -ri 's#input/ui/src/test/integration/org/richfaces/component/select/TestSelectMouseSelection.java#framework/src/test/integration/org/richfaces/component/select/TestSelectMouseSelection.java#g' *
    sed -ri 's#input/ui/src/test/integration/org/richfaces/component/select/TestSelectValidation.java#framework/src/test/integration/org/richfaces/component/select/TestSelectValidation.java#g' *
    sed -ri 's#output/ui/src/test/integration/org/richfaces/component/RF12768_Test.java#framework/src/test/integration/org/richfaces/component/tabPanel/RF12768_Test.java#g' *
    ...

Multiple file occurences
------------------------

The source files has moved, but it has many occurences in the source tree.

We can't automatically change patch files, but we can detect the collisions and let you know:

    for original in `cat $PATCHES/*  | egrep '^(---|\+\+\+) ' | egrep -v '\/dev\/null'  | sed -r 's#(---|\+\+\+) (a|b)/(([^ ]+)/([^\/]+))$#\3#g' | uniq`; do basename=`basename $original`; count=`find -name $basename | wc -l`; if [[ "$count" -gt 1 ]]; then pushd $PATCHES/ >/dev/null; echo ""; echo "$basename ($original)"; find -type f -exec grep -q $basename {} \; -print; popd >/dev/null; find -name $basename; fi ; done

The expected script's output:

    pom.xml (common/api/pom.xml)
    ./0005-RF-12768-Added-an-integration-test-that-confirms-the.patch
    ./framework/pom.xml
    ./framework/target/classes/META-INF/maven/org.richfaces/richfaces-framework/pom.xml
    ./dist/pom.xml
    ./build/pom.xml
    ...

The output lists:

* what file changed has been detected
* what is the original file path in the richfaces4 repository
* which patches are affected
* where given file occurs in the richfaces5 repository


New files
---------

When new file was introduced, it's up to you to decide where new file should be placed.

You need to modify patch files manually, we can just detect what files has been introduced:

    for original in `cat $PATCHES/*  | egrep '^(---|\+\+\+) ' | egrep -v '\/dev\/null'  | sed -r 's#(---|\+\+\+) (a|b)/(([^ ]+)/([^\/]+))$#\3#g' | uniq`; do basename=`basename $original`; count=`find -name $basename | wc -l`; if [[ "$count" -eq 0 ]]; then pushd $PATCHES/ >/dev/null; echo ""; echo "$basename"; find -type f -exec grep -q $basename {} \; -print; popd >/dev/null; fi ; done

    RF12765_Test.java
    ./0012-RF-12765-Updated-the-test-to-fail-on-tomcat-6.patch

In order to eliminate number of changes, you can run following heuristic for automatic
renaming of newly introduced files:

Change some of the left files
-----------------------------

    cd ~/patches/components
    sed -ri 's#(input|core|misc|iteration|output)\/ui#framework#g' *

    cd ~/patches/core
    sed -ri 's#core\/impl#framework#g' *

At the end, you will likely need to move some files further, this just helps to move them to right module.

Be sure to review commits in the richfaces5 repository after applying patches.

Applying patches
----------------

At the end, once patches are prepared, you can run:

    cd richfaces5/
    git am ~/patches/components/*
    git am ~/patches/core/*
    ...
