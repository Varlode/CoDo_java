package com.app.codo_java.data;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.app.codo_java.model.Fault;
import com.app.codo_java.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RemoteDataSources {

	private static final String REF_DATES = "01/01/2020";
	private final FirebaseDatabase dbRef;
	private FirebaseStorage stoRef;

	public RemoteDataSources(FirebaseDatabase dbRef) {
		this.dbRef = dbRef;
	}

	public RemoteDataSources(FirebaseDatabase dbRef, FirebaseStorage stoRef) {
		this.dbRef = dbRef;
		this.stoRef = stoRef;
	}

	public static String sha256(final String base) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-256");
			final byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
			final StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				final String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public <E> void getListOfValue(
		@NonNull String path, resultCallback<ArrayList<E>> callback
	)
	{
		dbRef.getReference(path).orderByValue().addValueEventListener(new ValueEventListener() {

			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				ArrayList<E> arrayList = new ArrayList<>(0);
				for (DataSnapshot parent : dataSnapshot.getChildren()) {
					arrayList.add((E) parent.getValue());
				}
				callback.onResult(arrayList);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				callback.onResult(null);
			}
		});
	}

	public <E> void getListOfAllValue(
		@NonNull String path, resultCallback<ArrayList<E>> callback
	)
	{
		dbRef.getReference(path).addValueEventListener(new ValueEventListener() {

			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				ArrayList<E> arrayList = new ArrayList<>(0);
				for (DataSnapshot parent : snapshot.getChildren()) {
					for (DataSnapshot data : parent.getChildren()) {
						arrayList.add((E) data.getValue());
					}
				}
				callback.onResult(arrayList);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				callback.onResult(null);
			}
		});
	}

	public <A, B> void getListOfAllPair(
		@NonNull String path, resultCallback<ArrayList<Pair<A, B>>> callback
	)
	{
		dbRef.getReference(path).addValueEventListener(new ValueEventListener() {

			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				ArrayList<Pair<A, B>> arrayList = new ArrayList<>(0);
				for (DataSnapshot parent : snapshot.getChildren()) {
					for (DataSnapshot child : parent.getChildren()) {
						ArrayList<A> pair = new ArrayList<>(0);
						for (DataSnapshot data : child.getChildren()) {
							pair.add((A) data.getValue());
						}
						arrayList.add((Pair<A, B>) Pair.create(pair.get(0), pair.get(1)));
					}
				}
				callback.onResult(arrayList);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				callback.onResult(null);
			}
		});
	}

	public <A, B> void getListOfPair(
		@NonNull String path, resultCallback<ArrayList<Pair<A, B>>> callback
	)
	{
		dbRef.getReference(path).addValueEventListener(new ValueEventListener() {

			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				ArrayList<Pair<A, B>> arrayList = new ArrayList<>(0);
				for (DataSnapshot parent : snapshot.getChildren()) {
					ArrayList<A> pair = new ArrayList<>(0);
					for (DataSnapshot data : parent.getChildren()) {
						pair.add((A) data.getValue());
					}
					arrayList.add((Pair<A, B>) Pair.create(pair.get(0), pair.get(1)));
				}
				callback.onResult(arrayList);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				callback.onResult(null);
			}
		});
	}

	public void getUser(@NonNull String path, resultCallback<User> callback) {
		dbRef.getReference(path).addValueEventListener(new ValueEventListener() {

			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				User u = dataSnapshot.getValue(User.class);
				callback.onResult(u);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				callback.onResult(null);
			}
		});
	}

	public void getUserSingle(@NonNull String path, resultCallback<User> callback) {
		dbRef.getReference(path).addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				User u = dataSnapshot.getValue(User.class);
				callback.onResult(u);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				callback.onResult(null);
			}
		});
	}

	public <E> void updateValue(@NonNull String path, E value, successCallback callback) {
		dbRef.getReference(path).setValue(value).addOnSuccessListener(
			unused -> callback.onSuccess(true)).addOnFailureListener(
			e -> callback.onSuccess(false));
	}

	public <E, V> void updateMap(
		@NonNull String path, HashMap<E, V> value, successCallback callback
	)
	{
		dbRef.getReference(path).setValue(value).addOnSuccessListener(
			unused -> callback.onSuccess(true)).addOnFailureListener(
			e -> callback.onSuccess(false));
	}

	public void deleteValue(@NonNull String path, successCallback callback) {
		dbRef.getReference(path).removeValue().addOnSuccessListener(
			unused -> callback.onSuccess(true)).addOnFailureListener(
			e -> callback.onSuccess(false));
	}

	public void updateUser(@NonNull String path, User user, successCallback callback) {
		dbRef.getReference(path).setValue(user).addOnSuccessListener(
			unused -> callback.onSuccess(true)).addOnFailureListener(
			e -> callback.onSuccess(false));
	}

	public void deleteUser(@NonNull String path, successCallback callback) {
		dbRef.getReference(path).removeValue().addOnSuccessListener(
			unused -> callback.onSuccess(true)).addOnFailureListener(
			e -> callback.onSuccess(false));
	}

	public void updateImage(@NonNull String path, Bitmap bitmap, resultCallback<String> callback) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
		byte[] data = byteArrayOutputStream.toByteArray();
		@SuppressLint("DefaultLocale")
		String imgPath = String.format("%s.jpg", sha256(Arrays.toString(data)));

		stoRef.getReference(path + imgPath).putBytes(data)
		      .addOnSuccessListener(
			      new OnSuccessListener<UploadTask.TaskSnapshot>() {

				      @Override
				      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
					      stoRef.getReference(path + imgPath)
					            .getDownloadUrl()
					            .addOnSuccessListener(
						            new OnSuccessListener<Uri>() {

							            @Override
							            public void onSuccess(Uri uri) {
								            callback.onResult(String.valueOf(uri));
							            }
						            });
				      }
			      })
		      .addOnFailureListener(exception -> callback.onResult(""));
	}

	public void deleteImage(@NonNull String path, successCallback callback) {
		stoRef
			.getReference(path)
			.delete()
			.addOnSuccessListener(unused -> callback.onSuccess(true))
			.addOnFailureListener(e -> callback.onSuccess(false));
	}

	public void getListOfFault(
		@NonNull String path, String dayFrom, String dayTo, String fault, String role, String unit,
		resultCallback<ArrayList<Fault>> callback
	)
	{
		String daysBetweenFrom = String.valueOf(theDaysBetween(dayFrom));
		String daysBetweenTo = String.valueOf(theDaysBetween(dayTo));

		dbRef
			.getReference(path)
			.orderByKey()
			.startAt(daysBetweenFrom)
			.endAt(daysBetweenTo)
			.addListenerForSingleValueEvent(new ValueEventListener() {

				@Override
				public void onDataChange(@NonNull DataSnapshot snapshot) {
					ArrayList<Fault> arrayList = new ArrayList<>(0);
					for (DataSnapshot parent : snapshot.getChildren()) {
						for (DataSnapshot child : parent.getChildren()) {
							String
								dataFault = child.child("fault").getValue(String.class),
								dataRole = child.child("roleInput").getValue(String.class),
								dataUnit = child.child("unit").getValue(String.class);
							if (fault == null) dataFault = null;
							if (role == null) dataRole = null;
							if (unit == null) dataUnit = null;
							if (Objects.equals(fault, dataFault) && Objects.equals(role, dataRole)
							    && Objects.equals(unit, dataUnit))
							{
								arrayList.add(child.getValue(Fault.class));
							}
						}
					}
					callback.onResult(arrayList);
				}

				@Override
				public void onCancelled(@NonNull DatabaseError error) {
					callback.onResult(null);
				}
			});
	}

	public void updateFault(@NonNull String path, Fault data, successCallback callback) {
		dbRef.getReference(path).push().setValue(data).addOnSuccessListener(
			unused -> callback.onSuccess(true)).addOnFailureListener(
			e -> callback.onSuccess(false));
	}

	public void deleteFault(@NonNull String path, successCallback callback) {
		dbRef.getReference(path).removeValue().addOnSuccessListener(
			unused -> callback.onSuccess(true)).addOnFailureListener(
			e -> callback.onSuccess(false));
	}

	private long theDaysBetween(String today) {
		long daysBetween = 1;
		try {
			@SuppressLint("SimpleDateFormat")
			Date refDate = new SimpleDateFormat("dd/MM/yyyy").parse(REF_DATES);
			@SuppressLint("SimpleDateFormat")
			Date nowDate = new SimpleDateFormat("dd/MM/yyyy").parse(today);
			long diff = nowDate.getTime() - refDate.getTime();
			daysBetween = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return daysBetween;
	}

	public interface resultCallback<T> {

		void onResult(T result);

	}

	public interface successCallback {

		void onSuccess(boolean isSuccess);

	}

}
