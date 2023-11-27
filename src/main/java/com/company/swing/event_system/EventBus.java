package com.company.swing.event_system;

import com.company.swing.event_system.payload.DirectorySelectedPayload;
import com.company.swing.event_system.payload.ProcessingFilePayload;
import com.company.swing.event_system.payload.SearchEndedPayload;
import com.company.swing.event_system.payload.StartSearchPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventBus {

    public static List<Consumer<DirectorySelectedPayload>> subscribersOfDirectorySelected = new ArrayList<>();
    public static List<Consumer<StartSearchPayload>> subscribersOfSearchStarted = new ArrayList<>();
    public static List<Consumer<SearchEndedPayload>> subscribersOfSearchEnded = new ArrayList<>();
    public static List<Consumer<ProcessingFilePayload>> subscribersOfProcessingFile = new ArrayList<>();
    public static void fireOnDirectorySelected(DirectorySelectedPayload payload) {
        subscribersOfDirectorySelected.forEach(v -> v.accept(payload));
    }

    public static void fireOnSearchStarted(StartSearchPayload payload) {
        subscribersOfSearchStarted.forEach(v -> v.accept(payload));
    }

    public static void fireOnSearchEnded(SearchEndedPayload payload) {
        subscribersOfSearchEnded.forEach(v -> v.accept(payload));
    }

    public static void fireOnProcessingFile(ProcessingFilePayload payload) {
        subscribersOfProcessingFile.forEach(v -> v.accept(payload));
    }
}
