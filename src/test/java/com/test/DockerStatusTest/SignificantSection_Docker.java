package com.test.DockerStatusTest;
import com.jcraft.jsch.*;

import org.testng.annotations.Test;

import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStreamReader;

import java.net.InetAddress;

public class SignificantSection_Docker {
	
	@Test(priority=1)

	public void pingWeb() {

		 String ipAddress = "172.20.24.7"; // Replace with the IP address you want to ping
		 try {
			 InetAddress address = InetAddress.getByName(ipAddress);
			 boolean reachable = address.isReachable(5000); // Timeout in milliseconds     
			 assert reachable : "IP Address " + ipAddress + " is not reachable.";
			 System.out.println("IP Address " + ipAddress + " is reachable.");
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
	}

	@Test(priority=2)
	public void containerStatus() {

		String vmIpAddress = "172.20.24.7";
		String username = "hbp";
		String password = "Health#123"; // Consider using a more secure method to handle passwords
		String containerId = "65f7a53cf587"; // Replace with your container's ID
		if (containerId.isEmpty()) {
			System.out.println("Container ID is required.");
			return;
		}
		try {
			JSch jsch = new JSch();
			Session session = jsch.getSession(username, vmIpAddress, 22);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no"); // Consider using a more secure method for host key verification
			session.connect();

			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand("docker inspect --format='{{.State.Status}}' " + containerId);
			channel.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				assert line.equals("running") : "Container is not in the expected state.";
			}

			channel.disconnect();
			session.disconnect();

        } catch (Exception e) {

            e.printStackTrace();
        }
	}

}
