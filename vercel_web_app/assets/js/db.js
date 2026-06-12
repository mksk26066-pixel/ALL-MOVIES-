const DB_KEY = 'vidbunker_db';

const defaultData = {
    videos: [
        {
            id: '1',
            title: 'Operation Nightingale: Briefing 04',
            description: 'Highly confidential briefing on secure deployment tactics. Node access restricted.',
            duration: '24:18',
            views: 12400,
            uploadDate: Date.now() - 7200000,
            videoUrl: 'https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
            thumbnailUrl: 'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&q=80&w=1000'
        },
        {
            id: '2',
            title: 'Protocol Zeta Overview',
            description: 'Standard operating procedures for encrypted data streams.',
            duration: '14:05',
            views: 3100,
            uploadDate: Date.now() - 86400000,
            videoUrl: 'https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4',
            thumbnailUrl: 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&q=80&w=1000'
        }
    ]
};

function initDb() {
    if (!localStorage.getItem(DB_KEY)) {
        localStorage.setItem(DB_KEY, JSON.stringify(defaultData));
    }
}

function getDb() {
    initDb();
    return JSON.parse(localStorage.getItem(DB_KEY));
}

function saveDb(data) {
    localStorage.setItem(DB_KEY, JSON.stringify(data));
}

function getAllVideos() {
    return getDb().videos.sort((a,b) => b.uploadDate - a.uploadDate);
}

function getVideo(id) {
    return getDb().videos.find(v => v.id === id);
}

function addVideo(video) {
    const db = getDb();
    video.id = Date.now().toString();
    video.views = 0;
    video.uploadDate = Date.now();
    db.videos.push(video);
    saveDb(db);
    return video;
}

function incrementViews(id) {
    const db = getDb();
    const vid = db.videos.find(v => v.id === id);
    if(vid) {
        vid.views++;
        saveDb(db);
    }
}

function timeAgo(dateParam) {
  if (!dateParam) return '';
  const date = typeof dateParam === 'object' ? dateParam : new Date(dateParam);
  const now = new Date();
  const seconds = Math.round((now - date) / 1000);
  const minutes = Math.round(seconds / 60);
  const hours = Math.round(minutes / 60);
  const days = Math.round(hours / 24);

  if (seconds < 60) return `${seconds}s ago`;
  if (minutes < 60) return `${minutes}m ago`;
  if (hours < 24) return `${hours}h ago`;
  return `${days}d ago`;
}
