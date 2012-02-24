
%CIG - Single Pass - Mod Single Pass, Average Linkage, Complete Linkage,
%Single Linkage
yBar=[0.920 0.764 0.985 0.642 0.845;
      0.724 0.695 0.935 0.418 0.799;
      0.743 0.544 0.954 0.361 0.714;
      0.760 0.484 0.649 0.366 0.521;
      0.758 0.602 0.661 0.369 0.466;
      0.560 0.450 0.655 0.280 0.431
      ]
bar(yBar);
title('Max F Measure for the five Datasets For different Algorithm');
leg = legend('UW-CAN','4 Universities', 'Syskill', 'Reuters', '20 News Group');
set(gca,'XTickLabel',{'CIG','SP','MSP', 'Avg. Link', 'Comp. Link', 'Sin. Link'},  'FontSize', 8) 
xlabel('Algorithms');
ylabel('F Measure');
