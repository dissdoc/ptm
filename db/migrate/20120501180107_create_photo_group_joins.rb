class CreatePhotoGroupJoins < ActiveRecord::Migration
  def change
    create_table :photo_group_joins do |t|
      t.integer :photo_id
      t.integer :group_id

      t.timestamps
    end
  end
end
