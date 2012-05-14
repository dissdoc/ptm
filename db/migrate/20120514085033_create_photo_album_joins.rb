class CreatePhotoAlbumJoins < ActiveRecord::Migration
  def change
    create_table :photo_album_joins do |t|
      t.integer :album_id
      t.integer :photo_id

      t.timestamps
    end
  end
end
