package net.lax1dude.eaglercraft.internal.lwjgl;

import java.util.function.Consumer;

import org.json.JSONObject;

import net.lax1dude.eaglercraft.EaglercraftVersion;
import net.lax1dude.eaglercraft.internal.IClientConfigAdapter;
import net.lax1dude.eaglercraft.internal.IClientConfigAdapterHooks;

/**
 * Copyright (c) 2022 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
public class DesktopClientConfigAdapter implements IClientConfigAdapter {

	public static final IClientConfigAdapter instance = new DesktopClientConfigAdapter();

	private final DesktopClientConfigAdapterHooks hooks = new DesktopClientConfigAdapterHooks();

	public String getDefaultLocale() {
		return "en_US";
	}

	@Override
	public String getServerToJoin() {
		return null;
	}

	@Override
	public String getWorldsDB() {
		return "worlds";
	}

	public String getResourcePacksDB() {
		return "resourcePacks";
	}

	public JSONObject getIntegratedServerOpts() {
		return new JSONObject("{\"container\":null,\"worldsDB\":\"worlds\"}");
	}

	@Override
	public boolean isCheckGLErrors() {
		return false;
	}

	public boolean isCheckShaderGLErrors() {
		return false;
	}

	public boolean isDemo() {
		return EaglercraftVersion.forceDemoMode;
	}

	public boolean allowUpdateSvc() {
		return false;
	}

	public boolean allowUpdateDL() {
		return false;
	}

	public boolean isEnableDownloadOfflineButton() {
		return false;
	}

	public String getDownloadOfflineButtonLink() {
		return null;
	}

	public boolean useSpecialCursors() {
		return false;
	}

	public boolean isLogInvalidCerts() {
		return false;
	}

	public boolean isCheckRelaysForUpdates() {
		return false;
	}

	public boolean isEnableSignatureBadge() {
		return false;
	}

	public boolean isAllowVoiceClient() {
		return false;
	}

	public boolean isAllowFNAWSkins() {
		return true;
	}

	public String getLocalStorageNamespace() {
		return EaglercraftVersion.localStorageNamespace;
	}

	public boolean isEnableMinceraft() {
		return true;
	}

	public boolean isEnableServerCookies() {
		return true;
	}

	public boolean isAllowServerRedirects() {
		return true;
	}

	public boolean isOpenDebugConsoleOnLaunch() {
		return false;
	}

	public boolean isForceWebViewSupport() {
		return false;
	}

	public boolean isEnableWebViewCSP() {
		return true;
	}

	public boolean isAllowBootMenu() {
		return false;
	}

	public boolean isForceProfanityFilter() {
		return false;
	}

	public boolean isEaglerNoDelay() {
		return false;
	}

	public boolean isRamdiskMode() {
		return false;
	}

	public boolean isEnforceVSync() {
		return false;
	}

	public IClientConfigAdapterHooks getHooks() {
		return hooks;
	}

	private static class DesktopClientConfigAdapterHooks implements IClientConfigAdapterHooks {

		@Override
		public void callLocalStorageSavedHook(String key, String base64) {

		}

		@Override
		public String callLocalStorageLoadHook(String key) {
			return null;
		}

		@Override
		public void callCrashReportHook(String crashReport, Consumer<String> customMessageCB) {

		}

		@Override
		public void callScreenChangedHook(String screenName, int scaledWidth, int scaledHeight, int realWidth,
				int realHeight, int scaleFactor) {

		}

	}

}