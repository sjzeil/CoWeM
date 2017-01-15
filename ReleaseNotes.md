= Release Notes

== v1.6 January 16, 2017

* Outline has a new output format, "calendar". This is a simple list
  of the dated items from the outline. The accompanying Javascript
  will compare each item in the list to the current date, marking the
  items as past, recent, current, & future.  CSS formats each
  separately.

  This is suitable for access via a short iframe to provide a
  date-sensitive list of upcoming events. Such an iframe can be
  inserted into the "modules" listing or into a Blackboard item.

* In the "modules" format, adding the metadata

        Calendar: yes

    to the top of the outline.md will insert an Upcoming Events
    section after the preamble.

* Modules output format now defaults to fully expanded. (Note that
  v1.5 allowed this page to remember which modules had been collapsed
  on previous visits.  An expand/collapse button has been added for
  the preamble.

	
  
== v1.5 January 14, 2017

* "modules" format of outline now remembers which modules were
  expanded/compressed and restores state when page is revisited.

* Improved formatting of tables in slides format.


== v1.3 -- v1.4

* Various bug fixes.

== v1.2 August 13, 2016

* Various bug fixes.
* Deployment uses copy instead of sync to avoid overwriting externally
  generated files on the server.
* Groups with a build.gradle will now have
    - all *.* files in the group directory
	- an index.html file generated to prevent walking the directory
      via a web browser.

== v1.1 August 13, 2016

* Various bug fixes.
* Support for rsync from Windows (Mingw/MSYS version)


== v1.0 July 10, 2016

Initial release.

Supports

* Creation of documents in scroll, pages, slides, directory,
  modules, & topics formats.
* URL shortcuts to facilitate inter-document linking.
* Deployment of website via ssh or rsync
* Packaging of website as zip archive or thin Blackboard export
    * "Thin" exports push navigation links & calendar events. The navigation
       links point back to a conventional website for the actual content.
    * "Fat" exports push navigation links, calendar events, and all course
       content.
       
Features deferred to future releases:
* EPUB & Mobi publication
* IMS CC exports (thin and fat)
* Dating of documents from a combination of file mod dates &
  (if the course materials are in a Git repository) Git log info.
