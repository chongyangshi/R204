clear;

files = {'Q2', 'Q3-X5', 'Q3-X5000', 'Q4-X5', 'Q4-X5000', 'Q5-X5', 'Q5-X5000', 'Q6-X5', 'Q6-X5000', 'Q7-X5', 'Q7-X5000'}; 
descriptions = {'Q2: Simple delays'...
    'Q3: sum() with built-in mutex, X=5'...
    'Q3: sum() with built-in mutex, X=5000'...
    'Q4: sum() with TATAS mutex lock, X=5'...
    'Q4: sum() with TATAS mutex lock, X=5000'...
    'Q5: sum() with TATAS R-W lock, X=5'...
    'Q5: sum() with TATAS R-W lock, X=5000'...
    'Q6: sum() with flag-based R-W lock, X=5'...
    'Q6: sum() with flag-based R-W lock, X=5000'...
    'Q7: sum() with version number scheme, X=5'...
    'Q7: sum() with version number scheme, X=5000'...
};
    
CORES = 4;
HW_THREADS = 8;
MAX_THREADS = 16;
MAX_Y_5000 = 8.5;
MAX_Y_5 = 0.2;

datum = {};
for i = 1:size(files, 2)
    filename = [files{i} '.csv'];
    datum{i} = csvread(filename, 1, 0);
end

datum_stats = {};
for i = 1:size(datum, 2)
    datum_stats{i} = [mean(datum{i}); max(datum{i}) - mean(datum{i}); mean(datum{i}) - min(datum{i})];
end

% Q2 dedicated
x = linspace(1, MAX_THREADS, MAX_THREADS);
y = datum_stats{1}(1,:);

plot(x,datum{1}, '.'); hold on;

xlim([0 MAX_THREADS]);
ylim([0 MAX_Y_5000/2]);
set(gca,'XTick',(0:1:MAX_THREADS));
plot([CORES CORES], [0 MAX_Y_5000/2]); 
text(CORES, 0.25, '# Cores');
fig = plot([HW_THREADS HW_THREADS], [0 MAX_Y_5000/2]); 
text(HW_THREADS, 0.25, '# HW Threads');
title(descriptions{1});
xlabel('Number of threads spawn');
ylabel('Execution time (seconds)');

grid on;
grid minor;

errn = datum_stats{1}(3,:);
errp = datum_stats{1}(2,:);
errorbar(x, y, errn, errp);

hold off;
saveas(fig, [files{1} '.png']);

% Rest of the data
means5 = {};
means5desp = {};
means5000 = {};
means5000desp = {};

for i=2:size(files, 2)
    
    if contains(files{i}, 'X5000')
        max_y = MAX_Y_5000;
    else
        max_y = MAX_Y_5;
    end
    
    x = linspace(1, MAX_THREADS, MAX_THREADS);
    y = datum_stats{i}(1,:);
    
    if contains(files{i}, 'X5000')
        means5000{end+1} = y;
        means5000desp{end+1} = descriptions{i};
    elseif contains(files{i}, 'X5')
        means5{end+1} = y;
        means5desp{end+1} = descriptions{i};
    end

    plot(x, datum{i}, '.'); hold on;

    xlim([0 MAX_THREADS]);
    ylim([0 max_y]);
    set(gca,'XTick',(0:1:MAX_THREADS));
    plot([CORES CORES], [0 max_y]); 
    text(CORES, 0.25, '# Cores');
    fig = plot([HW_THREADS HW_THREADS], [0 max_y]); 
    text(HW_THREADS, 0.25, '# HW Threads');
    title(descriptions{i});
    xlabel('Number of threads spawn');
    ylabel('Execution time (seconds)');

    grid on;
    grid minor;

    errn = datum_stats{i}(3,:);
    errp = datum_stats{i}(2,:);
    errorbar(x, y, errn, errp);

    hold off;
    saveas(fig, [pwd '/' files{i} '.png']);
end

% X = 5 crossplot
figure; 
for i = 1:size(means5, 2)
    x = linspace(1, MAX_THREADS, MAX_THREADS);
    y = means5{i};
    plot(x, y, '-', 'DisplayName', means5desp{i});
    hold on;
end
xlim([0 MAX_THREADS/2+1]);
ylim([0 MAX_Y_5/2]);
set(gca,'XTick',(0:1:MAX_THREADS/2));
xlabel('Number of threads spawn');
ylabel('Execution time (seconds)');
title('Comparison between concurrency mechanisms, X=5');
grid on;
legend('show');
plot([CORES CORES], [0 MAX_Y_5/2]); 
text(CORES, 0.25, '# Cores');
plot([HW_THREADS HW_THREADS], [0 MAX_Y_5/2]); 
text(HW_THREADS, 0.25, '# HW Threads');
saveas(gcf, [pwd '/X5All.png']);

hold off;

% X = 5000 crossplot
figure; 
for i = 1:size(means5000, 2)
    x = linspace(1, MAX_THREADS, MAX_THREADS);
    y = means5000{i};
    plot(x, y, '-', 'DisplayName', means5000desp{i});
    hold on;
end
xlim([0 MAX_THREADS/2+1]);
ylim([0 MAX_Y_5000/2]);
set(gca,'XTick',(0:1:MAX_THREADS/2));
xlabel('Number of threads spawn');
ylabel('Execution time (seconds)');
title('Comparison between concurrency mechanisms, X=5000');
grid on;
legend('show', 'Location', 'southeast');
plot([CORES CORES], [0 MAX_Y_5000/2]); 
text(CORES, MAX_Y_5000/2-0.25, '# Cores');
plot([HW_THREADS HW_THREADS], [0 MAX_Y_5000/2]); 
text(HW_THREADS, MAX_Y_5000/2-0.25, '# HW Threads');
saveas(gcf, [pwd '/X5000All.png']);

hold off;
