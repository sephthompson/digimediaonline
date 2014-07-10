package com.devcru.digimediaonline;

public class MediaLibrary {
	
	// We're setting the MediaLibrary as a class to encapsulate it

	private static Object[] mediaLibrary = {
		// We need to account for leading zeros, since 00005 comes out as '5'
		// Otherwise, SKUs cannot have leading zeros
		00005, "Starts With Zero So This Can't Be Selected", 5.99, 1,
		20003, "Radiohead", 10.99, 1,
		20041, "Metallica", 5.99, 1,
		50002, "Saving Private Ryan", 12.99, 2,
		50013, "Schindler's List", 12.99, 2,
		50089, "Mission Impossible 3", 13.99, 2,
		51006, "House MD Season 1", 15.99, 2,
		51234, "I am Legend", 15.99, 2,
		52034, "28 Days Later", 12.99, 2,
		52035, "28 Weeks Later", 12.99, 2,
		53200, "The Avengers", 20.99, 3,
		53049, "Iron Man 3", 21.99, 3,
	};

	public Object[] getMediaLibrary() {
		return mediaLibrary;
	}

	public void setMediaLibrary(Object[] mediaLibrary) {
		MediaLibrary.mediaLibrary = mediaLibrary;
	}
	
	public void prettifyMediaLib() {
		// Prettifies our mediaLibrary with rows and columns for human-readability
		for (int i = 0; i < getMediaLibrary().length; i++) {
			if (i != 0 && i % 4 == 0) {
				System.out.print("\n");
			}
			if (i % 4 != 0) {
				System.out.print(" | ");
			}
			System.out.print(getMediaLibrary()[i]);
		}
	}
	
}