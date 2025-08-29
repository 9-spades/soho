document.addEventListener('DOMContentLoaded', () => {
    const queryParams = new URLSearchParams(window.location.search);
    new Countdown(
        'https://5qa6wrp7aej62k677izcvgku7e0qcqqv.lambda-url.us-west-1.on.aws/'
        +
        `?targetDate=${queryParams.get('targetDate')}&timezone=${queryParams.get('timezone')}`,
        {
            targetDate: document.getElementById('target-date'),
            days: document.getElementById('days'),
            hours: document.getElementById('hours'),
            minutes: document.getElementById('minutes'),
            seconds: document.getElementById('seconds')
        }
    ).init();
});

class Countdown {
    constructor(url, component) {
        this.url = url;
        this.component = component;
    }
    
    init() {
        this.fetch();
    }
    
    async fetch() {
        try {
            const status = await fetch(this.url);
            if(!status.ok) throw new Error(`HTTP ${status.status}: ${status.statusText}`);
            const body = await status.json();
            this.status = {
                days: body.days,
                hours: body.hours,
                minutes: body.minutes,
                seconds: body.seconds,
                targetDate: body.targetDate,
                timezone: body.timezone
            };
            this.display();
            this.start();
        } catch(error) {
            console.error('Error fetching countdown:', error);
        }
    }
    
    start() {
        this.interval = setInterval(() => {
            this.status.seconds--;
            if(this.status.seconds < 0) {
                this.status.seconds = 59;
                this.status.minutes--;
            }
            if(this.status.minutes < 0) {
                this.status.minutes = 59;
                this.status.hours--;
            }
            if(this.status.hours < 0) {
                this.status.hours = 23;
                this.status.days--;
            }
            if(this.status.days < 0) {
                this.status.days = 0;
                clearInterval(this.interval);
                return;
            }
            this.update();
        }, 1000);
    }
    
    display() {
        const options = {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            timeZoneName: 'long',
            timezone: this.status.timezone
        };
        this.component.targetDate.textContent = `${new Date(this.status.targetDate).toLocaleString('en-US', options)}`;
        this.update();
    }
    
    update() {
        this.component.days.textContent = String(this.status.days).padStart(2, '0');
        this.component.hours.textContent = String(this.status.hours).padStart(2, '0');
        this.component.minutes.textContent = String(this.status.minutes).padStart(2, '0');
        this.component.seconds.textContent = String(this.status.seconds).padStart(2, '0');
    }
}
