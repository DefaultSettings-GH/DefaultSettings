package de.pt400c.defaultsettings;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.versioning.ComparableVersion;

public class UpdateContainer {

	private ExecutorService tpe = Executors.newFixedThreadPool(1);
	private Status status = Status.UNKNOWN;
	private String onlineVersion = null;

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void setOnlineVersion(String version){
		this.onlineVersion = version;
	}
	
	public String getOnlineVersion(){
		return this.onlineVersion;
	}

	public Status getStatus() {
		return this.status;
	}

	public void update() {
		this.setStatus(Status.CHECKING);
		this.tpe.submit(new Runnable() {

			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				//System.out.println();
				
				ModContainer mc = FMLCommonHandler.instance().findContainerFor(DefaultSettings.getInstance());
				ForgeVersion.Status status = ForgeVersion.Status.PENDING;
				ComparableVersion target = null;
				try {
					InputStream con = openUrlStream(new URL("https://gist.githubusercontent.com/PT400C/be22046792a7859688f655f1a5f83975/raw/976f2796b2f145c75cba258fe40259b5ca5555ac/ds-updates.json"));
					String data = new String(ByteStreams.toByteArray(con), "UTF-8");
					con.close();
					Map<String, Object> json = new Gson().fromJson(data, Map.class);

					Map<String, String> promos = (Map<String, String>) json.get("promos");
					//String display_url = (String) json.get("homepage");
					String MC_Version = Loader.instance().getMCVersionString().split(" ")[1];
					String rec = promos.get(MC_Version + "-recommended");
					String lat = promos.get(MC_Version + "-latest");
					ComparableVersion current = new ComparableVersion(mc.getVersion());

					if (rec != null) {
						ComparableVersion recommended = new ComparableVersion(rec);
						int diff = recommended.compareTo(current);

						if (diff == 0)
							status = ForgeVersion.Status.UP_TO_DATE;
						else if (diff < 0) {
							status = ForgeVersion.Status.AHEAD;
							if (lat != null) {
								ComparableVersion latest = new ComparableVersion(lat);
								if (current.compareTo(latest) < 0) {
									status = ForgeVersion.Status.OUTDATED;
									target = latest;
								}
							}
						} else {
							status = ForgeVersion.Status.OUTDATED;
							target = recommended;
						}
					} else if (lat != null) {
						ComparableVersion latest = new ComparableVersion(lat);
						if (current.compareTo(latest) < 0) {
							status = ForgeVersion.Status.BETA_OUTDATED;
							target = latest;
						} else
							status = ForgeVersion.Status.BETA;
					} else
						status = ForgeVersion.Status.BETA;
				} catch (IOException e) {
					DefaultSettings.log.log(Level.SEVERE, "Error while checking for updates: ", e);
					DefaultSettings.getUpdater().setStatus(UpdateContainer.Status.ERROR);
					return;
				}
				if(target != null)
					DefaultSettings.getUpdater().setOnlineVersion(target.toString());
				
				switch (status) {
				case BETA: 
					DefaultSettings.getUpdater().setStatus(UpdateContainer.Status.UP_TO_DATE);
					break;
				case BETA_OUTDATED: 
					DefaultSettings.getUpdater().setStatus(UpdateContainer.Status.OUTDATED);
					break;
				case UP_TO_DATE: 
					DefaultSettings.getUpdater().setStatus(UpdateContainer.Status.UP_TO_DATE);
					break;
				case AHEAD: 
					DefaultSettings.getUpdater().setStatus(UpdateContainer.Status.UP_TO_DATE);
					break;
				case OUTDATED: 
					DefaultSettings.getUpdater().setStatus(UpdateContainer.Status.OUTDATED);
					break;
				case PENDING: 
					DefaultSettings.getUpdater().setStatus(UpdateContainer.Status.CHECKING);
					break;
				case FAILED: 

				default: 
					DefaultSettings.getUpdater().setStatus(UpdateContainer.Status.ERROR);
				
				}
			}
		});
		

	}

	public static InputStream openUrlStream(URL url) throws IOException {
		InputStream stream = null;
		URLConnection con = url.openConnection();
		con.setConnectTimeout(5000);
		if (con instanceof HttpURLConnection) {
			HttpURLConnection huc = (HttpURLConnection) con;
			huc.setInstanceFollowRedirects(false);
			int responseCode = huc.getResponseCode();
			if (responseCode == 200) {
				stream = con.getInputStream();
				return stream;
			}
		}
		throw new IOException("Couldn't create a proper connection to the remote host");
	}
	
	public static enum Status {
		UNKNOWN, CHECKING, UP_TO_DATE, OUTDATED, ERROR;
	}
	
	static class ForgeVersion {
		public static enum Status
	    {
	        PENDING,
	        FAILED,
	        UP_TO_DATE,
	        OUTDATED,
	        AHEAD,
	        BETA,
	        BETA_OUTDATED
	    }
	}

}