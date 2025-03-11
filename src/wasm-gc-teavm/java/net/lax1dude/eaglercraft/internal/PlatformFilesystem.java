/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.internal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.lax1dude.eaglercraft.internal.vfs2.EaglerFileSystemException;
import net.lax1dude.eaglercraft.internal.wasm_gc_teavm.IndexedDBFilesystem;

public class PlatformFilesystem {

	private static final Logger logger = LogManager.getLogger("PlatformFilesystem");

	public static IEaglerFilesystem initializePersist(String dbName) {
		try {
			//Stupid fix because my dumbass left the database name blank
			if(dbName.equals(PlatformRuntime.getClientConfigAdapter().getWorldsDB())) {
				dbName = "";
			}
			return IndexedDBFilesystem.createFilesystem(dbName);
		}catch(Throwable t) {
			logger.error("Could not open IndexedDB filesystem: {}", dbName);
			logger.error(t);
			return null;
		}
	}

	public static class FilesystemDatabaseLockedException extends EaglerFileSystemException {
		public FilesystemDatabaseLockedException(String message) {
			super(message);
		}
	}

	public static class FilesystemDatabaseInitializationException extends EaglerFileSystemException {
		public FilesystemDatabaseInitializationException(String message) {
			super(message);
		}
	}

}