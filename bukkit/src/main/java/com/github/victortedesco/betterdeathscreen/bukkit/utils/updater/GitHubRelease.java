package com.github.victortedesco.betterdeathscreen.bukkit.utils.updater;

import lombok.Getter;

@Getter
public class GitHubRelease {

    private final String name;
    private final String tag_name;
    private final String html_url;
    private final boolean prerelease;
    private final boolean draft;

    public GitHubRelease(String name, String tag_name, String html_url, boolean prerelease, boolean draft) {
        this.name = name;
        this.tag_name = tag_name;
        this.html_url = html_url;
        this.prerelease = prerelease;
        this.draft = draft;
    }
}
