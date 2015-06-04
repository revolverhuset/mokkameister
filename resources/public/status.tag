<status>
    <div>Siste brygg: { new Date(data.lastbrew.time).toLocaleString() } av { data.lastbrew.by }.</div>

    this.data = {}

    load() {
        var self = this
        $.ajax({
            url: opts.url,
            dataType: 'json',
            cache: false,
            success: function(d) {
                self.data = d
                self.update()
            }})
    }

    this.load()
    setInterval(this.load, opts.interval)
</status>
