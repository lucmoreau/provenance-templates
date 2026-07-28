#!/usr/bin/env python3
"""Crawl https://openprovenance.org/templates/ and collect all URLs.

Starting from the seed URL, download the page, extract every URL, and repeat
the process for any discovered URL that shares the seed's prefix. The result is
a list of unique URLs sorted alphabetically.
"""

import sys
import re
from collections import deque
from urllib.parse import urljoin, urldefrag
from urllib.request import Request, urlopen
from urllib.error import URLError, HTTPError

SEED = "https://openprovenance.org/templates/"

# Match href="..." and src="..." attributes (single or double quoted).
LINK_RE = re.compile(r'(?:href|src)\s*=\s*["\']([^"\']+)["\']', re.IGNORECASE)


def fetch(url):
    """Return the text body of url, or None if it can't be fetched as text."""
    req = Request(url, headers={"User-Agent": "template-crawler/1.0"})
    try:
        with urlopen(req, timeout=30) as resp:
            ctype = resp.headers.get_content_type()
            if not (ctype.startswith("text/") or ctype in
                    ("application/xhtml+xml", "application/xml")):
                return None
            charset = resp.headers.get_content_charset() or "utf-8"
            return resp.read().decode(charset, errors="replace")
    except (HTTPError, URLError, ValueError, TimeoutError) as e:
        print(f"  ! failed to fetch {url}: {e}", file=sys.stderr)
        return None


def extract_urls(base_url, html):
    """Extract absolute, fragment-stripped URLs from html."""
    urls = set()
    for match in LINK_RE.findall(html):
        absolute = urljoin(base_url, match)
        absolute, _ = urldefrag(absolute)  # drop #fragment
        if absolute.startswith(("http://", "https://")):
            urls.add(absolute)
    return urls


def crawl(seed):
    all_urls = set()        # every URL ever discovered
    visited = set()         # pages we've already downloaded
    queue = deque([seed])

    while queue:
        page = queue.popleft()
        if page in visited:
            continue
        visited.add(page)
        print(f"crawling {page}", file=sys.stderr)

        html = fetch(page)
        if html is None:
            continue

        for url in extract_urls(page, html):
            all_urls.add(url)
            # Only recurse into pages sharing the seed prefix.
            if url.startswith(seed) and url not in visited:
                queue.append(url)

    return all_urls


def main():
    seed = sys.argv[1] if len(sys.argv) > 1 else SEED
    urls = crawl(seed)
    for url in sorted(urls):
        print(url)


if __name__ == "__main__":
    main()
