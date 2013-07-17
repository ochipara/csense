function DrawMessageRouteUsages(path)
load(sprintf('%s/%s', path, 'MessageRouteUsages.mat'));
hatched = false;
dpi = 150;

% all routes
usages = MessageRouteUsages';
figure('Name', 'Audiology - Message Route Usages');
%colormap gray
barh(usages./1000, 'stack');
xlabel('Thread CPU Time(us)');
ylabel('Message Routes');
ylabels = {'fileMonitor','form','sound','gps','gps-logger'};
title('Audiology Message Route Usages');
legend('C1W', 'C1E','C2W','C2E','C3W','C3E','C4W','C4E');
set(gca,'YTickLabelMode', 'manual', 'YTickLabel', ylabels);
grid on;
saveas(gcf, sprintf('%s/%s', path, 'all.png'), 'png');
if(hatched)
    imwrite(applyhatch_plus(gcf, '/|.\-x+c',[],dpi), sprintf('%s/%s', path, 'all-hatched.png'),'png');
end

% fileMonitor
usage = usages(1,1:8);
usage(2, 1:8) = 0;
figure('Name', 'Audiology - fileMonitor Message Route Usage');
%colormap gray
barh(usage./1000, 'stack');
xlabel('Thread CPU Time(us)');
ylabel('Message Route');
ylabels = {'fileMonitor',''};
title('fileMonitor Message Route Usage');
legend('fileMonitor waiting time', 'fileMonitor exec time','form waiting time','form exec time','uploadComplete waiting time','uploadComplete exec time','tap waiting time','tap exec time');
set(gca,'YTickLabelMode', 'manual', 'YTickLabel', ylabels);
grid on;
saveas(gcf, sprintf('%s/%s', path, 'fileMonitor.png'),'png');
if(hatched)
    imwrite(applyhatch_plus(gcf, '/|.\-x+c',[],dpi), sprintf('%s/%s', path, 'fileMonitor-hatched.png'),'png');
end

% form
usage = usages(2,1:8);
usage(2, 1:8) = 0;
figure('Name', 'Audiology - form Message Route Usage');
%colormap gray
barh(usage./1000000, 'stack');
xlabel('Thread CPU Time(ms)');
ylabel('Message Route');
ylabels = {'form',''};
title('form Message Route Usage');
legend('form waiting time', 'form exec time','httpUpload waiting time','httpUpload exec time','uploadComplete waiting time','uploadComplete exec time','tap waiting time','tap exec time');
set(gca,'YTickLabelMode', 'manual', 'YTickLabel', ylabels);
grid on;
saveas(gcf, sprintf('%s/%s', path, 'form.png'),'png');
if(hatched)
    imwrite(applyhatch_plus(gcf, '/|.\-x+c',[],dpi), sprintf('%s/%s', path, 'form-hatched.png'),'png');
end

% sound
usage = usages(3,1:8);
usage(2, 1:8) = 0;
figure('Name', 'Audiology - sound Message Route Usage');
%colormap gray
barh(usage./1000, 'stack');
xlabel('Thread CPU Time(us)');
ylabel('Message Route');
ylabels = {'sound',''};
title('sound Message Route Usage');
legend('sound waiting time', 'sound exec time','syncQueue1 waiting time','syncQueue1 exec time','todisk waiting time','todisk exec time','tap waiting time','tap exec time');
set(gca,'YTickLabelMode', 'manual', 'YTickLabel', ylabels);
grid on;
saveas(gcf, sprintf('%s/%s', path, 'sound.png'),'png');
if(hatched)
    imwrite(applyhatch_plus(gcf, '/|.\-x+c',[],dpi), sprintf('%s/%s', path, 'sound-hatched.png'),'png');
end

% gps
usage = usages(4,1:8);
usage(2, 1:8) = 0;
figure('Name', 'Audiology - gps Message Route Usage');
%colormap gray
barh(usage./1000, 'stack');
xlabel('Thread CPU Time(us)');
ylabel('Message Route');
ylabels = {'gps',''};
title('gps Message Route Usage');
legend('gps waiting time', 'gps exec time','gps-logger waiting time','gps-logger exec time','tap waiting time','tap exec time');
set(gca,'YTickLabelMode', 'manual', 'YTickLabel', ylabels);
grid on;
saveas(gcf, sprintf('%s/%s', path, 'gps.png'),'png');
if(hatched)
    imwrite(applyhatch_plus(gcf, '/|.\-x+c',[],dpi), sprintf('%s/%s', path, 'gps-hatched.png'), 'png');
end

% gps logger
usage = usages(5,1:8);
usage(2, 1:8) = 0;
figure('Name', 'Audiology - gps-logger Message Route Usage');
%colormap gray
barh(usage./1000, 'stack');
xlabel('Thread CPU Time(us)');
ylabel('Message Route');
ylabels = {'gps-logger',''};
title('gps-logger Message Route Usage');
legend('gps-logger waiting time', 'gps-logger exec time','syncQueue0 waiting time', 'syncQueue0 exec time', 'gpsToDisk waiting time','gpsToDisk exec time','tap waiting time','tap exec time');
set(gca,'YTickLabelMode', 'manual', 'YTickLabel', ylabels);
grid on;
saveas(gcf, sprintf('%s/%s', path, 'gps-logger.png'), 'png');
if(hatched)
    imwrite(applyhatch_plus(gcf, '/|.\-x+c',[],dpi), sprintf('%s/%s', path, 'gps-loggers-hatched.png'),'png');
end
end