/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.io.location;

/**
 * Some IOLocations may belong to a common group.
 * <p>
 * This was originally added for FTP locations: when the user switches to a
 * location that is not in the same group as the previous: then dispose needs to
 * be called.
 *
 */
public interface IOLocationGroup {
	/**
	 * Notify this group than an object is interested in continually using the
	 * <code>IOLocations</code> that belong to this group.
	 * <p>
	 * For example: a <code>LocationPane</code> will be a consumer as long as it
	 * is visible. Also an object that is writing to locations will call this
	 * method.
	 * 
	 * @return a unique key identifying the consumer. When the consumer is
	 *         finished using this group, it should call
	 *         <code>releaseConsumer(key)</code> When a group has no outstanding
	 *         keys: it may want to flush resources, or log out.
	 */
	public Object addConsumer();

	/**
	 * Notify this group than an external object is no longer interested in
	 * using the <code>IOLocations</code> in this group.
	 * 
	 * @param key
	 *            a key previously created by <code>addConsumer()</code>.
	 */
	public void releaseConsumer(Object key);

	/**
	 * Return true if there is a consumer still listening to this group. Once
	 * <code>releaseConsumer()</code> has been called for every consumer that
	 * was added: this will return false;
	 */
	public boolean isActive();
}