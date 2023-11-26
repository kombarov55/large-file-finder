package com.company.swing.event_system;

import com.company.swing.event_system.payload.DirectorySelectedPayload;
import com.company.swing.event_system.payload.SearchEndedPayload;
import com.company.swing.event_system.payload.StartSearchPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventBus {

    public static List<Consumer<DirectorySelectedPayload>> subscribersOfDirectorySelected = new ArrayList<>();
    public static List<Consumer<StartSearchPayload>> subscribersOfSearchStarted = new ArrayList<>();
    public static List<Consumer<SearchEndedPayload>> subscribersOfSearchEnded = new ArrayList<>();
    public static void onDirectorySelected(DirectorySelectedPayload payload) {
        subscribersOfDirectorySelected.forEach(v -> v.accept(payload));
    }

    public static void onSearchStarted(StartSearchPayload payload) {
        subscribersOfSearchStarted.forEach(v -> v.accept(payload));
    }

    public static void onSearchEnded(SearchEndedPayload payload) {
        subscribersOfSearchEnded.forEach(v -> v.accept(payload));
    }
}
